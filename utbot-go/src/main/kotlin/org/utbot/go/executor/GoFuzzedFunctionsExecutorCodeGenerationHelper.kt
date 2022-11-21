package org.utbot.go.executor

import org.utbot.go.api.GoUtFile
import org.utbot.go.api.GoUtFuzzedFunction
import org.utbot.go.logic.EachExecutionTimeoutsMillisConfig
import org.utbot.go.simplecodegeneration.GoFileCodeBuilder
import org.utbot.go.simplecodegeneration.generateFuzzedFunctionCall
import org.utbot.go.util.goRequiredImports

internal object GoFuzzedFunctionsExecutorCodeGenerationHelper {

    private val alwaysRequiredImports =
        setOf("context", "encoding/json", "errors", "fmt", "math", "os", "reflect", "strings", "testing", "time", "log")

    fun generateExecutorTestFileGoCode(
        sourceFile: GoUtFile,
        fuzzedFunctions: List<GoUtFuzzedFunction>,
        eachExecutionTimeoutsMillisConfig: EachExecutionTimeoutsMillisConfig,
        executorTestFunctionName: String,
        rawExecutionResultsFileName: String,
    ): String {
        val fileCodeBuilder = GoFileCodeBuilder()

        fileCodeBuilder.setPackage(sourceFile.packageName)

        val additionalImports = mutableSetOf<String>()
        fuzzedFunctions.forEach { (_, fuzzedParametersValues) ->
            fuzzedParametersValues.forEach { additionalImports += it.goRequiredImports }
        }
        fileCodeBuilder.setImports(alwaysRequiredImports + additionalImports)

        val executorTestFunctionCode =
            generateExecutorTestFunctionCode(
                fuzzedFunctions,
                eachExecutionTimeoutsMillisConfig,
                executorTestFunctionName,
                rawExecutionResultsFileName
            )
        fileCodeBuilder.addTopLevelElements(
            CodeTemplates.topLevelHelperStructsAndFunctions + listOf(executorTestFunctionCode)
        )

        return fileCodeBuilder.buildCodeString()
    }

    // TODO: use more convenient code generation
    private fun generateExecutorTestFunctionCode(
        fuzzedFunctions: List<GoUtFuzzedFunction>,
        eachExecutionTimeoutsMillisConfig: EachExecutionTimeoutsMillisConfig,
        executorTestFunctionName: String,
        rawExecutionResultsFileName: String,
    ): String {
        val codeSb = StringBuilder()
        codeSb.append("func $executorTestFunctionName(t *testing.T) {")
        codeSb.append("\n\texecutionResults := __UtBotGoExecutorRawExecutionResults__{Results: []__UtBotGoExecutorRawExecutionResult__{")

        fuzzedFunctions.forEach { fuzzedFunction ->
            val fuzzedFunctionCall = generateFuzzedFunctionCall(fuzzedFunction)
            val function = fuzzedFunction.function
            val timeoutMillis = eachExecutionTimeoutsMillisConfig[function]

            codeSb.append("\n\t\t__executeFunctionForUtBotGoExecutor__(")
                .append("\"${function.name}\", $timeoutMillis*time.Millisecond, ")
                .append("func() []interface{} {")

            if (function.resultTypes.isEmpty()) {
                codeSb.append("\n\t\t\t$fuzzedFunctionCall")
                codeSb.append("\n\t\t\treturn []interface{}{}")
            } else {
                codeSb.append("\n\t\t\treturn __wrapResultValuesForUtBotGoExecutor__($fuzzedFunctionCall)")
            }

            codeSb.append("\n\t\t}),")
        }

        codeSb.append("\n")
        codeSb.append(
            """
                }}
                
            	jsonBytes, toJsonErr := json.MarshalIndent(executionResults, "", "  ")
            	__checkErrorAndExitToUtBotGoExecutor__(toJsonErr)

            	const resultsFilePath = "$rawExecutionResultsFileName"
            	writeErr := os.WriteFile(resultsFilePath, jsonBytes, os.ModePerm)
            	__checkErrorAndExitToUtBotGoExecutor__(writeErr)
            }
            
        """.trimIndent()
        )

        return codeSb.toString()
    }

    private object CodeTemplates {

        private val primitiveValueStruct = """
            type __PrimitiveValue__ struct {
            	Type  string `json:"type"`
            	Value string `json:"value"`
            }
        """.trimIndent()

        private val fieldValueStruct = """
            type __FieldValue__ struct {
            	Name  string      `json:"name"`
            	Value interface{} `json:"value"`
            }
        """.trimIndent()

        private val structValueStruct = """
            type __StructValue__ struct {
            	Type  string           `json:"type"`
            	Value []__FieldValue__ `json:"value"`
            }
        """.trimIndent()

        private val arrayValueStruct = """
            type __ArrayValue__ struct {
            	Type        string      `json:"type"`
            	ElementType string      `json:"elementType"`
            	Length      int         `json:"length"`
            	Value       interface{} `json:"value"`
            }
        """.trimIndent()

        private val panicMessageStruct = """
            type __UtBotGoExecutorRawPanicMessage__ struct {
            	RawResultValue  interface{} `json:"rawResultValue"`
            	ImplementsError bool        `json:"implementsError"`
            }
        """.trimIndent()

        private val rawExecutionResultStruct = """
            type __UtBotGoExecutorRawExecutionResult__ struct {
            	FunctionName    string                              `json:"functionName"`
            	TimeoutExceeded bool                                `json:"timeoutExceeded"`
            	ResultRawValues []interface{}                       `json:"resultRawValues"`
            	PanicMessage    *__UtBotGoExecutorRawPanicMessage__ `json:"panicMessage"`
            }
        """.trimIndent()

        private val rawExecutionResultsStruct = """
            type __UtBotGoExecutorRawExecutionResults__ struct {
            	Results []__UtBotGoExecutorRawExecutionResult__ `json:"results"`
            }
        """.trimIndent()

        private val checkErrorFunction = """
            func __checkErrorAndExitToUtBotGoExecutor__(err error) {
            	if err != nil {
            		log.Fatal(err.Error())
            	}
            }
        """.trimIndent()

        private val convertFloat64ValueToStringFunction = """
            func __convertFloat64ValueToStringForUtBotGoExecutor__(value float64) string {
            	const outputNaN = "NaN"
            	const outputPosInf = "+Inf"
            	const outputNegInf = "-Inf"
            	switch {
            	case math.IsNaN(value):
            		return fmt.Sprint(outputNaN)
            	case math.IsInf(value, 1):
            		return fmt.Sprint(outputPosInf)
            	case math.IsInf(value, -1):
            		return fmt.Sprint(outputNegInf)
            	default:
            		return fmt.Sprintf("%#v", value)
            	}
            }
        """.trimIndent()

        private val convertValueToResultValueFunction = """
            //goland:noinspection GoPreferNilSlice
            func __convertValueToResultValue__(valueOfRes reflect.Value) (interface{}, error) {
            	const outputComplexPartsDelimiter = "@"

            	switch valueOfRes.Kind() {
            	case reflect.Int, reflect.Int8, reflect.Int16, reflect.Int32, reflect.Int64:
            		return __PrimitiveValue__{
            			Type:  valueOfRes.Kind().String(),
            			Value: fmt.Sprintf("%#v", valueOfRes.Interface()),
            		}, nil
            	case reflect.Uint, reflect.Uint8, reflect.Uint16, reflect.Uint32, reflect.Uint64:
            		return __PrimitiveValue__{
            			Type:  valueOfRes.Kind().String(),
            			Value: fmt.Sprintf("%#v", valueOfRes.Interface()),
            		}, nil
            	case reflect.Float32, reflect.Float64:
            		return __PrimitiveValue__{
            			Type:  valueOfRes.Kind().String(),
            			Value: __convertFloat64ValueToStringForUtBotGoExecutor__(valueOfRes.Float()),
            		}, nil
            	case reflect.Complex64, reflect.Complex128:
            		value := valueOfRes.Complex()
            		realPartString := __convertFloat64ValueToStringForUtBotGoExecutor__(real(value))
            		imagPartString := __convertFloat64ValueToStringForUtBotGoExecutor__(imag(value))
            		return __PrimitiveValue__{
            			Type:  valueOfRes.Kind().String(),
            			Value: fmt.Sprintf("%v%v%v", realPartString, outputComplexPartsDelimiter, imagPartString),
            		}, nil
            	case reflect.String:
            		return __PrimitiveValue__{
            			Type:  reflect.String.String(),
            			Value: fmt.Sprintf("%#v", valueOfRes.String()),
            		}, nil
            	case reflect.Struct:
            		fields := reflect.VisibleFields(valueOfRes.Type())
            		resultValues := make([]__FieldValue__, len(fields))
            		for i, field := range fields {
            			res, err := __convertValueToResultValue__(valueOfRes.FieldByName(field.Name))
            			fmt.Println(res)
            			__checkErrorAndExitToUtBotGoExecutor__(err)

            			resultValues[i] = __FieldValue__{
            				Name:  field.Name,
            				Value: res,
            			}
            		}
            		return __StructValue__{
            			Type:  strings.Replace(valueOfRes.Type().Name(), valueOfRes.Type().PkgPath()+".", "", 1),
            			Value: resultValues,
            		}, nil
            	case reflect.Array:
            		elem := valueOfRes.Type().Elem()
            		elementType := strings.Replace(elem.String(), elem.PkgPath()+".", "", 1)
            		arrayElementValues := []interface{}{}
            		for i := 0; i < valueOfRes.Len(); i++ {
            			arrayElementValue, err := __convertValueToResultValue__(valueOfRes.Index(i))
            			__checkErrorAndExitToUtBotGoExecutor__(err)

            			arrayElementValues = append(arrayElementValues, arrayElementValue)
            		}
            		return __ArrayValue__{
            			Type:        valueOfRes.Type().String(),
            			ElementType: elementType,
            			Length:      len(arrayElementValues),
            			Value:       arrayElementValues,
            		}, nil
            	default:
            		return nil, errors.New("unsupported result type: " + valueOfRes.Type().String())
            	}
            }
        """.trimIndent()

        private val executeFunctionFunction = """
            func __executeFunctionForUtBotGoExecutor__(
            	functionName string,
            	timeoutMillis time.Duration,
            	wrappedFunction func() []interface{},
            ) __UtBotGoExecutorRawExecutionResult__ {
            	ctxWithTimeout, cancel := context.WithTimeout(context.Background(), timeoutMillis)
            	defer cancel()

            	done := make(chan __UtBotGoExecutorRawExecutionResult__, 1)
            	go func() {
            		executionResult := __UtBotGoExecutorRawExecutionResult__{
            			FunctionName:    functionName,
            			TimeoutExceeded: false,
            			ResultRawValues: []interface{}{},
            			PanicMessage:    nil,
            		}
            		panicked := true
            		defer func() {
            			panicMessage := recover()
            			if panicked {
            				_, implementsError := panicMessage.(error)
            				resultValue, err := __convertValueToResultValue__(reflect.ValueOf(panicMessage))
            				__checkErrorAndExitToUtBotGoExecutor__(err)
            				
            				executionResult.PanicMessage = &__UtBotGoExecutorRawPanicMessage__{
            				    RawResultValue:  resultValue,
            					ImplementsError: implementsError,
                            }
            			}
            			done <- executionResult
            		}()

            		resultValues := wrappedFunction()
            		executionResult.ResultRawValues = resultValues
            		panicked = false
            	}()

            	select {
            	case timelyExecutionResult := <-done:
            		return timelyExecutionResult
            	case <-ctxWithTimeout.Done():
            		return __UtBotGoExecutorRawExecutionResult__{
            			FunctionName:    functionName,
            			TimeoutExceeded: true,
            			ResultRawValues: []interface{}{},
            			PanicMessage:    nil,
            		}
            	}
            }
        """.trimIndent()

        private val wrapResultValuesFunction = """
            //goland:noinspection GoPreferNilSlice
            func __wrapResultValuesForUtBotGoExecutor__(values ...any) []interface{} {
            	rawValues := []interface{}{}
            	for _, value := range values {
            		resultValue, err := __convertValueToResultValue__(reflect.ValueOf(value))
            		__checkErrorAndExitToUtBotGoExecutor__(err)
            		
            		rawValues = append(rawValues, resultValue)
            	}
            	return rawValues
            }
        """.trimIndent()

        val topLevelHelperStructsAndFunctions = listOf(
            primitiveValueStruct,
            fieldValueStruct,
            structValueStruct,
            arrayValueStruct,
            panicMessageStruct,
            rawExecutionResultStruct,
            rawExecutionResultsStruct,
            checkErrorFunction,
            convertFloat64ValueToStringFunction,
            convertValueToResultValueFunction,
            executeFunctionFunction,
            wrapResultValuesFunction,
        )
    }
}