# Assert

[![Build Status](https://travis-ci.org/stfsy/golang-assert.svg)](https://travis-ci.org/stfsy/golang-assert)

Two simple functions for soft unit test assertions in Go.

Yes, the [Go FAQ](https://golang.org/doc/faq#testing_framework) states the following:
> [..] testing frameworks tend to develop into mini-languages of their own, with conditionals and controls and printing mechanisms, but Go already has all those capabilities; why recreate them? We'd rather write tests in Go; it's one fewer language to learn and the approach keeps the tests straightforward and easy to understand.

But unit tests should be clean too, right? :)

Both Assertions are soft assertions. Failing tests will only be reported to the console via *t.Errorf()*.


## Assert equal
```go
var paul = Person{
	name: "Paul",
	age:  32}

func TestEqual(t *testing.T) {
	assert.Equal(t, paul, paul, "Paul equals paul")
}
```

## Assert not equal
```go
var paul = Person{
	name: "Paul",
	age:  32}

var peter = Person{
	name: "Peter",
	age:  21}

func TestNotEqual(t *testing.T) {
	assert.NotEqual(t, paul, peter, "Paul does not equal peter")
```

## Installation

```bash
go get github.com/stfsy/golang-assert
```

## License

This project is distributed under the MIT license.