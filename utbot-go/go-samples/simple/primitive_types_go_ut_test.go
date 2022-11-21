package simple

import(
	"github.com/stretchr/testify/assert"
	"math"
	"testing"
)

func TestMyPrintByUtGoFuzzer1(t *testing.T) {
	assert.NotPanics(t, func() { MyPrint("   ") })
}

func TestMyPrintByUtGoFuzzer2(t *testing.T) {
	assert.NotPanics(t, func() { MyPrint("string") })
}

func TestMyPrintByUtGoFuzzer3(t *testing.T) {
	assert.NotPanics(t, func() { MyPrint("") })
}

func TestMyPrintByUtGoFuzzer4(t *testing.T) {
	assert.NotPanics(t, func() { MyPrint("\n\t\r") })
}

func TestAbsByUtGoFuzzer1(t *testing.T) {
	actualVal := Abs(math.MaxInt)

	assert.Equal(t, 9223372036854775807, actualVal)
}

func TestAbsByUtGoFuzzer2(t *testing.T) {
	actualVal := Abs(1)

	assert.Equal(t, 1, actualVal)
}

func TestAbsByUtGoFuzzer3(t *testing.T) {
	actualVal := Abs(-1)

	assert.Equal(t, 1, actualVal)
}

func TestAbsByUtGoFuzzer4(t *testing.T) {
	actualVal := Abs(0)

	assert.Equal(t, 0, actualVal)
}

func TestAbsByUtGoFuzzer5(t *testing.T) {
	actualVal := Abs(math.MinInt)

	assert.Equal(t, -9223372036854775808, actualVal)
}

func TestDivOrPanicByUtGoFuzzer1(t *testing.T) {
	actualVal := DivOrPanic(-1, math.MaxInt)

	assert.Equal(t, 0, actualVal)
}

func TestDivOrPanicByUtGoFuzzer2(t *testing.T) {
	actualVal := DivOrPanic(math.MinInt, 1)

	assert.Equal(t, -9223372036854775808, actualVal)
}

func TestDivOrPanicByUtGoFuzzer3(t *testing.T) {
	actualVal := DivOrPanic(-1, math.MinInt)

	assert.Equal(t, 0, actualVal)
}

func TestDivOrPanicByUtGoFuzzer4(t *testing.T) {
	actualVal := DivOrPanic(1, 1)

	assert.Equal(t, 1, actualVal)
}

func TestDivOrPanicPanicsByUtGoFuzzer(t *testing.T) {
	assert.PanicsWithValue(t, "div by 0", func() { DivOrPanic(math.MaxInt, 0) })
}

func TestExtendedByUtGoFuzzer1(t *testing.T) {
	actualVal0, actualVal1, actualVal2 := Extended(math.MaxInt64, -1)

	assertMultiple := assert.New(t)
	assertMultiple.Equal(int64(-1), actualVal0)
	assertMultiple.Equal(int64(0), actualVal1)
	assertMultiple.Equal(int64(1), actualVal2)
}

func TestExtendedByUtGoFuzzer2(t *testing.T) {
	actualVal0, actualVal1, actualVal2 := Extended(0, math.MinInt64)

	assertMultiple := assert.New(t)
	assertMultiple.Equal(int64(-9223372036854775808), actualVal0)
	assertMultiple.Equal(int64(0), actualVal1)
	assertMultiple.Equal(int64(1), actualVal2)
}

func TestExtendedByUtGoFuzzer3(t *testing.T) {
	actualVal0, actualVal1, actualVal2 := Extended(1, math.MinInt64)

	assertMultiple := assert.New(t)
	assertMultiple.Equal(int64(1), actualVal0)
	assertMultiple.Equal(int64(1), actualVal1)
	assertMultiple.Equal(int64(0), actualVal2)
}

func TestExtendedByUtGoFuzzer4(t *testing.T) {
	actualVal0, actualVal1, actualVal2 := Extended(-1, 1)

	assertMultiple := assert.New(t)
	assertMultiple.Equal(int64(-1), actualVal0)
	assertMultiple.Equal(int64(1), actualVal1)
	assertMultiple.Equal(int64(0), actualVal2)
}

func TestExtendedByUtGoFuzzer5(t *testing.T) {
	actualVal0, actualVal1, actualVal2 := Extended(math.MaxInt64, 1)

	assertMultiple := assert.New(t)
	assertMultiple.Equal(int64(1), actualVal0)
	assertMultiple.Equal(int64(0), actualVal1)
	assertMultiple.Equal(int64(1), actualVal2)
}

func TestArraySumByUtGoFuzzer1(t *testing.T) {
	actualVal := ArraySum([10]int{-1, math.MaxInt, math.MinInt, 1, 0, math.MinInt, 0, math.MaxInt, math.MaxInt, 0})

	assert.Equal(t, 9223372036854775805, actualVal)
}

func TestArraySumByUtGoFuzzer2(t *testing.T) {
	actualVal := ArraySum([10]int{0, 1, 1, math.MaxInt, 0, math.MinInt, 0, math.MaxInt, math.MaxInt, 0})

	assert.Equal(t, -1, actualVal)
}

func TestArraySumByUtGoFuzzer3(t *testing.T) {
	actualVal := ArraySum([10]int{1, math.MaxInt, math.MaxInt, math.MinInt, math.MaxInt, math.MinInt, 0, math.MaxInt, math.MaxInt, 0})

	assert.Equal(t, 9223372036854775804, actualVal)
}

func TestArraySumByUtGoFuzzer4(t *testing.T) {
	actualVal := ArraySum([10]int{math.MaxInt, 0, 1, math.MaxInt, -1, math.MinInt, 0, math.MaxInt, math.MaxInt, 0})

	assert.Equal(t, 9223372036854775804, actualVal)
}

func TestArraySumByUtGoFuzzer5(t *testing.T) {
	actualVal := ArraySum([10]int{math.MaxInt, 1, math.MinInt, math.MinInt, 1, math.MinInt, 0, math.MaxInt, math.MaxInt, 0})

	assert.Equal(t, -1, actualVal)
}

func TestGenerateArrayOfIntegersByUtGoFuzzer1(t *testing.T) {
	actualVal := GenerateArrayOfIntegers(0)

	assert.Equal(t, [10]int{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, actualVal)
}

func TestGenerateArrayOfIntegersByUtGoFuzzer2(t *testing.T) {
	actualVal := GenerateArrayOfIntegers(math.MaxInt)

	assert.Equal(t, [10]int{9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807, 9223372036854775807}, actualVal)
}

func TestGenerateArrayOfIntegersByUtGoFuzzer3(t *testing.T) {
	actualVal := GenerateArrayOfIntegers(math.MinInt)

	assert.Equal(t, [10]int{-9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808, -9223372036854775808}, actualVal)
}

func TestGenerateArrayOfIntegersByUtGoFuzzer4(t *testing.T) {
	actualVal := GenerateArrayOfIntegers(1)

	assert.Equal(t, [10]int{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, actualVal)
}

func TestGenerateArrayOfIntegersByUtGoFuzzer5(t *testing.T) {
	actualVal := GenerateArrayOfIntegers(-1)

	assert.Equal(t, [10]int{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, actualVal)
}

func TestDistanceBetweenTwoPointsByUtGoFuzzer1(t *testing.T) {
	actualVal := DistanceBetweenTwoPoints(Point{x: math.Inf(-1), y: math.SmallestNonzeroFloat64}, Point{x: -1.1, y: math.SmallestNonzeroFloat64})

	assert.True(t, math.IsInf(actualVal, 1))
}

func TestDistanceBetweenTwoPointsByUtGoFuzzer2(t *testing.T) {
	actualVal := DistanceBetweenTwoPoints(Point{x: math.Inf(1), y: 0.0}, Point{x: math.MaxFloat64, y: 1.1})

	assert.True(t, math.IsInf(actualVal, 1))
}

func TestDistanceBetweenTwoPointsByUtGoFuzzer3(t *testing.T) {
	actualVal := DistanceBetweenTwoPoints(Point{x: math.NaN(), y: math.Inf(1)}, Point{x: math.MaxFloat64, y: math.MaxFloat64})

	assert.True(t, math.IsNaN(actualVal))
}

func TestDistanceBetweenTwoPointsByUtGoFuzzer4(t *testing.T) {
	actualVal := DistanceBetweenTwoPoints(Point{x: -1.1, y: 0.0}, Point{x: math.Inf(-1), y: math.Inf(-1)})

	assert.True(t, math.IsInf(actualVal, 1))
}

func TestDistanceBetweenTwoPointsByUtGoFuzzer5(t *testing.T) {
	actualVal := DistanceBetweenTwoPoints(Point{x: math.Inf(1), y: 0.0}, Point{x: math.Inf(-1), y: 0.0})

	assert.True(t, math.IsInf(actualVal, 1))
}

func TestGetCoordinatesOfMiddleBetweenTwoPointsByUtGoFuzzer1(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinatesOfMiddleBetweenTwoPoints(Point{x: math.NaN(), y: 1.1}, Point{x: math.NaN(), y: math.SmallestNonzeroFloat64})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.Equal(0.55, actualVal1)
}

func TestGetCoordinatesOfMiddleBetweenTwoPointsByUtGoFuzzer2(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinatesOfMiddleBetweenTwoPoints(Point{x: math.SmallestNonzeroFloat64, y: math.Inf(1)}, Point{x: 1.1, y: -1.1})

	assertMultiple := assert.New(t)
	assertMultiple.Equal(0.55, actualVal0)
	assertMultiple.True(math.IsInf(actualVal1, 1))
}

func TestGetCoordinatesOfMiddleBetweenTwoPointsByUtGoFuzzer3(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinatesOfMiddleBetweenTwoPoints(Point{x: math.Inf(1), y: math.MaxFloat64}, Point{x: math.MaxFloat64, y: 0.0})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsInf(actualVal0, 1))
	assertMultiple.Equal(8.988465674311579E307, actualVal1)
}

func TestGetCoordinatesOfMiddleBetweenTwoPointsByUtGoFuzzer4(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinatesOfMiddleBetweenTwoPoints(Point{x: 1.1, y: -1.1}, Point{x: math.NaN(), y: 1.1})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.Equal(0.0, actualVal1)
}

func TestGetCoordinatesOfMiddleBetweenTwoPointsByUtGoFuzzer5(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinatesOfMiddleBetweenTwoPoints(Point{x: math.SmallestNonzeroFloat64, y: -1.1}, Point{x: math.SmallestNonzeroFloat64, y: 0.0})

	assertMultiple := assert.New(t)
	assertMultiple.Equal(4.9E-324, actualVal0)
	assertMultiple.Equal(-0.55, actualVal1)
}

func TestGetCoordinateSumOfPointsByUtGoFuzzer1(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinateSumOfPoints([10]Point{{x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(-1), y: math.Inf(1)}, {x: math.Inf(1), y: 1.1}, {x: -1.1, y: 1.1}, {x: math.MaxFloat64, y: 0.0}, {x: math.NaN(), y: -1.1}})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.True(math.IsInf(actualVal1, 1))
}

func TestGetCoordinateSumOfPointsByUtGoFuzzer2(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinateSumOfPoints([10]Point{{x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.NaN(), y: math.Inf(-1)}, {x: math.Inf(-1), y: math.SmallestNonzeroFloat64}, {x: math.Inf(-1), y: math.Inf(1)}, {x: math.SmallestNonzeroFloat64, y: math.SmallestNonzeroFloat64}, {x: math.SmallestNonzeroFloat64, y: math.NaN()}})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.True(math.IsNaN(actualVal1))
}

func TestGetCoordinateSumOfPointsByUtGoFuzzer3(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinateSumOfPoints([10]Point{{x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: 1.1}, {x: math.Inf(1), y: math.Inf(1)}, {x: -1.1, y: math.MaxFloat64}, {x: math.Inf(-1), y: math.Inf(1)}, {x: -1.1, y: math.Inf(1)}})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.True(math.IsInf(actualVal1, 1))
}

func TestGetCoordinateSumOfPointsByUtGoFuzzer4(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinateSumOfPoints([10]Point{{x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.MaxFloat64, y: 0.0}, {x: -1.1, y: -1.1}, {x: math.NaN(), y: math.Inf(-1)}, {x: 1.1, y: math.Inf(-1)}, {x: math.Inf(-1), y: math.MaxFloat64}})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.True(math.IsNaN(actualVal1))
}

func TestGetCoordinateSumOfPointsByUtGoFuzzer5(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinateSumOfPoints([10]Point{{x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(-1), y: math.Inf(-1)}, {x: math.Inf(-1), y: 1.1}, {x: math.Inf(-1), y: -1.1}, {x: math.NaN(), y: 1.1}, {x: math.MaxFloat64, y: 1.1}})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.True(math.IsNaN(actualVal1))
}

func TestGetAreaOfCircleByUtGoFuzzer1(t *testing.T) {
	actualVal := GetAreaOfCircle(Circle{Center: Point{x: -1.1, y: math.SmallestNonzeroFloat64}, Radius: -1.1})

	assert.Equal(t, 3.8013271108436504, actualVal)
}

func TestGetAreaOfCircleByUtGoFuzzer2(t *testing.T) {
	actualVal := GetAreaOfCircle(Circle{Center: Point{x: math.Inf(1), y: 0.0}, Radius: math.NaN()})

	assert.True(t, math.IsNaN(actualVal))
}

func TestGetAreaOfCircleByUtGoFuzzer3(t *testing.T) {
	actualVal := GetAreaOfCircle(Circle{Center: Point{x: math.NaN(), y: 1.1}, Radius: math.Inf(-1)})

	assert.True(t, math.IsInf(actualVal, 1))
}

func TestGetAreaOfCircleByUtGoFuzzer4(t *testing.T) {
	actualVal := GetAreaOfCircle(Circle{Center: Point{x: -1.1, y: -1.1}, Radius: math.NaN()})

	assert.True(t, math.IsNaN(actualVal))
}

func TestGetAreaOfCircleByUtGoFuzzer5(t *testing.T) {
	actualVal := GetAreaOfCircle(Circle{Center: Point{x: 1.1, y: 1.1}, Radius: math.Inf(-1)})

	assert.True(t, math.IsInf(actualVal, 1))
}