package pool

import "cubalog/common"

type PooledStream struct {
	Subscribers StreamPool
}

func NewPooledStream(pool *StreamPool) PooledStream {
	stream := PooledStream{}
	if pool == nil {
		stream.Subscribers = NewSimplePool()
	} else {
		stream.Subscribers = *pool
	}
	return stream
}

func (t PooledStream) Write(record common.Record) {
	for t.Subscribers.HasNext() {
		t.Subscribers.Next().Write(record)
	}
}
