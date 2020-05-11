package pool

import (
	"fmt"
	"github.com/KirillBogatikov/Cuba/go-log/common"
	"github.com/stfsy/golang-assert"
	"testing"
)

type LogStreamMock struct {
	Name string
}

func (t LogStreamMock) Write(_ common.Record) {
	fmt.Println("It's a mock, Karl!")
}

func TestSimplePool_SubscribePass(t *testing.T) {
	pool := NewSimplePool()
	_ = pool.Subscribe(0, LogStreamMock{Name: "Anna"})
	err := pool.Subscribe(0, LogStreamMock{Name: "Boris"})
	assert.Equal(t, err, nil, "")
}

func TestSimplePool_SubscribeFail(t *testing.T) {
	pool := NewSimplePool()
	stream := LogStreamMock{"Clark"}
	_ = pool.Subscribe(0, stream)
	err := pool.Subscribe(0, stream)
	assert.NotEqual(t, err, nil, "")
}

func TestSimplePool_UnsubscribePass(t *testing.T) {
	pool := NewSimplePool()
	andrey := LogStreamMock{Name: "Andrey"}
	barbara := LogStreamMock{Name: "Barbara"}

	_ = pool.Subscribe(0, andrey)
	_ = pool.Subscribe(0, barbara)

	err := pool.Unsubscribe(andrey)
	assert.Equal(t, err, nil, "")
	assert.Equal(t, pool.Items[0].stream, barbara, "")
}

func TestSimplePool_UnsubscribeFail(t *testing.T) {
	pool := NewSimplePool()

	andrey := LogStreamMock{Name: "Andrey"}
	barbara := LogStreamMock{Name: "Barbara"}
	catherine := LogStreamMock{Name: "Catherine"}

	_ = pool.Subscribe(0, andrey)
	_ = pool.Subscribe(0, catherine)

	err := pool.Unsubscribe(barbara)
	assert.NotEqual(t, err, nil, "")
}

func TestSimplePool_Sort(t *testing.T) {
	pool := NewSimplePool()

	andrey := LogStreamMock{Name: "Andrey"}
	barbara := LogStreamMock{Name: "Barbara"}
	catherine := LogStreamMock{Name: "Catherine"}

	_ = pool.Subscribe(2, andrey)
	_ = pool.Subscribe(0, barbara)
	_ = pool.Subscribe(1, catherine)

	assert.Equal(t, pool.Items[0].stream, barbara, "")
	assert.Equal(t, pool.Items[1].stream, catherine, "")
	assert.Equal(t, pool.Items[2].stream, andrey, "")
}
