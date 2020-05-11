package pool

import (
	"cubalog/common"
	"errors"
	"sort"
)

type StreamPool interface {
	Next() common.LogStream
	HasNext() bool
	Reset()
}

type item struct {
	priority int
	stream   common.LogStream
}

type SimplePool struct {
	Items    []item
	position int
}

func NewSimplePool() SimplePool {
	return SimplePool{
		Items:    make([]item, 0),
		position: 0,
	}
}

func (t SimplePool) Next() common.LogStream {
	t.position += 1
	if t.HasNext() {
		t.position = 0
	}

	return t.Items[t.position].stream
}

func (t SimplePool) HasNext() bool {
	return t.position >= len(t.Items)
}

func (t SimplePool) Reset() {
	t.position = 0
}

func (t SimplePool) Subscribe(priority int, stream common.LogStream) error {
	for _, item := range t.Items {
		if item.stream == stream {
			return errors.New("Specified LogStream already subsribed")
		}
	}

	t.Items = append(t.Items, item{
		priority: priority,
		stream:   stream,
	})
	sort.Slice(t.Items, t.compareItems)

	return nil
}

func (t SimplePool) Unsubscribe(stream common.LogStream) error {
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
