package main

import (
	"go/ast"
	"go/token"
	"strconv"
)

type Visitor struct {
	counter int
}

func (f *Visitor) Visit(node ast.Node) ast.Visitor {
	switch n := node.(type) {
	case *ast.FuncDecl:
		if n.Body != nil {
			f.Visit(n.Body)
		}
	case *ast.BlockStmt:
		if n == nil {
			n = &ast.BlockStmt{List: []ast.Stmt{}}
		}
		n.List = f.addLineWithLoggingInTrace(n.List)
		for i, stmt := range n.List {
			if i != 0 {
				f.Visit(stmt)
			}
		}
	case *ast.IfStmt:
		if n.Init != nil {
			f.Visit(n.Init)
		}
		if n.Cond != nil {
			f.Visit(n.Cond)
		}
		f.Visit(n.Body)
		if n.Else == nil {
			n.Else = &ast.BlockStmt{}
		}
		switch stmt := n.Else.(type) {
		case *ast.IfStmt:
			n.Else = &ast.BlockStmt{List: []ast.Stmt{stmt}}
		}
		f.Visit(n.Else)
		return nil
	case *ast.ForStmt:
		if n.Init != nil {
			f.Visit(n.Init)
		}
		if n.Cond != nil {
			f.Visit(n.Cond)
		}
		if n.Post != nil {
			f.Visit(n.Post)
		}
		f.Visit(n.Body)
	case *ast.SwitchStmt:
		hasDefault := false
		if n.Body == nil {
			n.Body = &ast.BlockStmt{}
		}
		for _, stmt := range n.Body.List {
			if cas, ok := stmt.(*ast.CaseClause); ok && cas.List == nil {
				hasDefault = true
				break
			}
		}
		if !hasDefault {
			n.Body.List = append(n.Body.List, &ast.CaseClause{})
		}
		for _, stmt := range n.Body.List {
			f.Visit(stmt)
		}
	case *ast.TypeSwitchStmt:
		hasDefault := false
		if n.Body == nil {
			n.Body = &ast.BlockStmt{}
		}
		for _, stmt := range n.Body.List {
			if cas, ok := stmt.(*ast.CaseClause); ok && cas.List == nil {
				hasDefault = true
				break
			}
		}
		if !hasDefault {
			n.Body.List = append(n.Body.List, &ast.CaseClause{})
		}
		for _, stmt := range n.Body.List {
			f.Visit(stmt)
		}
	case *ast.SelectStmt:
		if n.Body == nil {
			return nil
		}
		for _, stmt := range n.Body.List {
			f.Visit(stmt)
		}
	case *ast.CaseClause:
		n.Body = f.addLineWithLoggingInTrace(n.Body)
		for i, stmt := range n.Body {
			if i != 0 {
				f.Visit(stmt)
			}
		}
	case *ast.CommClause:
		n.Body = f.addLineWithLoggingInTrace(n.Body)
		for i, stmt := range n.Body {
			if i != 0 {
				f.Visit(stmt)
			}
		}
	}
	return nil
}

func (f *Visitor) addLineWithLoggingInTrace(stmts []ast.Stmt) []ast.Stmt {
	var newList = []ast.Stmt{f.newLineWithLoggingInTrace()}
	newList = append(newList, stmts...)
	return newList
}

func (f *Visitor) newLineWithLoggingInTrace() ast.Stmt {
	f.counter++

	idx := &ast.BinaryExpr{
		X: &ast.CallExpr{
			Fun:  ast.NewIdent("len"),
			Args: []ast.Expr{ast.NewIdent("__traces__")},
		},
		Op: token.SUB,
		Y: &ast.BasicLit{
			Kind:  token.INT,
			Value: "1",
		},
	}
	traces := &ast.IndexExpr{
		X:     ast.NewIdent("__traces__"),
		Index: idx,
	}
	return &ast.AssignStmt{
		Lhs: []ast.Expr{traces},
		Tok: token.ASSIGN,
		Rhs: []ast.Expr{
			&ast.CallExpr{
				Fun: ast.NewIdent("append"),
				Args: []ast.Expr{
					&ast.IndexExpr{
						X:     ast.NewIdent("__traces__"),
						Index: idx,
					},
					ast.NewIdent(strconv.Itoa(f.counter)),
				},
			}},
	}
}

func createNewFunctionName(funcName string) string {
	return "__" + funcName + "__"
}
