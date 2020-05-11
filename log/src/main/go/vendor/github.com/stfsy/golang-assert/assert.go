package assert

import "testing"

func Equal(t *testing.T, actual interface{}, expected interface{}, message string) {
	assert(t, actual, expected, message, true)
}

func NotEqual(t *testing.T, actual interface{}, expected interface{}, message string) {
	assert(t, actual, expected, message, false)
}

func assert(t *testing.T, actual interface{}, expected interface{}, message string, expectation bool) {
	if (expected == actual) != expectation {
		t.Errorf("%s failed: Expected %v, but got %v", message, expected, actual)
	}
}
