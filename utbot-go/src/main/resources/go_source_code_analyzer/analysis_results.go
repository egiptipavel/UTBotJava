package main

import "go/token"

type AnalyzedPrimitiveType struct {
	Name string `json:"name"`
}

type AnalyzedField struct {
	Name string      `json:"name"`
	Type interface{} `json:"type"`
}

type AnalyzedStructType struct {
	Name            string          `json:"name"`
	ImplementsError bool            `json:"implementsError"`
	Fields          []AnalyzedField `json:"fields"`
}

type AnalyzedArrayType struct {
	Name        string      `json:"name"`
	ElementType interface{} `json:"elementType"`
	Length      int64       `json:"length"`
}

type AnalyzedFunctionParameter struct {
	Name string      `json:"name"`
	Type interface{} `json:"type"`
}

type AnalyzedFunction struct {
	Name                                string                      `json:"name"`
	ModifiedName                        string                      `json:"modifiedName"`
	Parameters                          []AnalyzedFunctionParameter `json:"parameters"`
	ResultTypes                         []interface{}               `json:"resultTypes"`
	ModifiedFunctionForCollectingTraces string                      `json:"modifiedFunctionForCollectingTraces"`
	NumberOfAllStatements               int                         `json:"numberOfAllStatements"`
	position                            token.Pos
}

type AnalysisResult struct {
	AbsoluteFilePath           string             `json:"absoluteFilePath"`
	PackageName                string             `json:"packageName"`
	AnalyzedFunctions          []AnalyzedFunction `json:"analyzedFunctions"`
	NotSupportedFunctionsNames []string           `json:"notSupportedFunctionsNames"`
	NotFoundFunctionsNames     []string           `json:"notFoundFunctionsNames"`
}

type AnalysisResults struct {
	Results []AnalysisResult `json:"results"`
}
