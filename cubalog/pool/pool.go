package pool

import (
	"errors"
	"github.com/KirillBogatikov/Test/cubalog/common"
	"sort"
)

type StreamPool interface {
	Next() common.LogStream
	HasNext() bool
	Reset()
}

type PoolItem struct {
	priority int
	stream   common.LogStream
}

type SimplePool struct {
	Items    []PoolItem
	position int
}

func NewSimplePool() *SimplePool {
	return &SimplePool{
		Items:    make([]PoolItem, 0),
		position: 0,
	}
}

func (t *SimplePool) Next() common.LogStream {
	if !t.HasNext() {
		t.position = 0
	}

	result := t.Items[t.position].stream
	t.position += 1
	return result
}

func (t *SimplePool) HasNext() bool {
	return t.position < len(t.Items)
}

func (t *SimplePool) Reset() {
	t.position = 0
}

func (t *SimplePool) Subscribe(priority int, stream common.LogStream) error {
	if stream == nil {
		return errors.New("LogStream can't be nil")
	}

	for _, item := range t.Items {
		if item.stream == stream {
			return errors.New("Specified LogStream already subscribed")
		}
	}

	t.Items = append(t.Items, PoolItem{
		priority: priority,
		stream:   stream,
	})
	sort.Slice(t.Items, t.compareItems)

	return nil
}

func (t *SimplePool) Unsubscribe(stream common.LogStream) error {
	if stream == nil {
		return errors.New("LogStream can't be nil")
	}

	index := -1
	for i, item := range t.Items {
		if item.stream == stream {
			index = i
			break
		}
	}

	if index < 0 {
		return errors.New("Specified LogStream doesn't subsribed")
	}

	t.Items = append(t.Items[:index], t.Items[index+1:]...)
	return nil
}

func (t SimplePool) compareItems(i, j int) bool {
	return t.Items[i].priority < t.Items[j].priority
}
