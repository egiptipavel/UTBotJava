package simple

import(
	"github.com/stretchr/testify/assert"
	"math"
	"testing"
)

func TestMyPrintByUtGoFuzzer(t *testing.T) {
	assert.NotPanics(t, func() { MyPrint("   ") })
}

func TestAbsByUtGoFuzzer1(t *testing.T) {
	actualVal := Abs(1)

	assert.Equal(t, 1, actualVal)
}

func TestAbsByUtGoFuzzer2(t *testing.T) {
	actualVal := Abs(math.MinInt)

	assert.Equal(t, -9223372036854775808, actualVal)
}

func TestDivOrPanicByUtGoFuzzer(t *testing.T) {
	actualVal := DivOrPanic(-1, 1)

	assert.Equal(t, -1, actualVal)
}

func TestDivOrPanicPanicsByUtGoFuzzer(t *testing.T) {
	assert.PanicsWithValue(t, "div by 0", func() { DivOrPanic(-1, 0) })
}

func TestExtendedByUtGoFuzzer(t *testing.T) {
	actualVal0, actualVal1, actualVal2 := Extended(-1, 1)

	assertMultiple := assert.New(t)
	assertMultiple.Equal(int64(-1), actualVal0)
	assertMultiple.Equal(int64(1), actualVal1)
	assertMultiple.Equal(int64(0), actualVal2)
}

func TestArraySumByUtGoFuzzer(t *testing.T) {
	actualVal := ArraySum([10]int{-1, math.MaxInt, math.MinInt, 1, math.MaxInt, math.MinInt, 0, math.MaxInt, math.MaxInt, 0})

	assert.Equal(t, -4, actualVal)
}

func TestGenerateArrayOfIntegersByUtGoFuzzer(t *testing.T) {
	actualVal := GenerateArrayOfIntegers(1)

	assert.Equal(t, [10]int{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, actualVal)
}

func TestDistanceBetweenTwoPointsByUtGoFuzzer(t *testing.T) {
	actualVal := DistanceBetweenTwoPoints(Point{x: -1.1, y: math.MaxFloat64}, Point{x: 0.0, y: math.NaN()})

	assert.True(t, math.IsNaN(actualVal))
}

func TestGetCoordinatesOfMiddleBetweenTwoPointsByUtGoFuzzer(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinatesOfMiddleBetweenTwoPoints(Point{x: -1.1, y: math.MaxFloat64}, Point{x: 0.0, y: math.NaN()})

	assertMultiple := assert.New(t)
	assertMultiple.Equal(-0.55, actualVal0)
	assertMultiple.True(math.IsNaN(actualVal1))
}

func TestGetCoordinateSumOfPointsByUtGoFuzzer(t *testing.T) {
	actualVal0, actualVal1 := GetCoordinateSumOfPoints([10]Point{{x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: math.Inf(1), y: math.Inf(1)}, {x: 0.0, y: math.Inf(1)}, {x: math.MaxFloat64, y: math.NaN()}, {x: math.Inf(-1), y: math.Inf(1)}, {x: math.NaN(), y: -1.1}, {x: math.Inf(1), y: math.Inf(-1)}, {x: math.Inf(-1), y: math.Inf(1)}})

	assertMultiple := assert.New(t)
	assertMultiple.True(math.IsNaN(actualVal0))
	assertMultiple.True(math.IsNaN(actualVal1))
}

func TestGetAreaOfCircleByUtGoFuzzer(t *testing.T) {
	actualVal := GetAreaOfCircle(Circle{Center: Point{x: 0.0, y: math.NaN()}, Radius: math.MaxFloat64})

	assert.True(t, math.IsInf(actualVal, 1))
}