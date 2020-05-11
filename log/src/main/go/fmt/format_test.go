package fmt

import (
	"cubalog/common"
	"encoding/json"
	"fmt"
	"github.com/stfsy/golang-assert"
	"testing"
	"time"
)

type User struct {
	Name    string `json:"FirstName"`
	Surname string
	Age     int
	Weight  float32
}

const (
	Tag         = "SomeTag"
	Message     = "Some text. Very interesting and short text"
	Base        = "[DEBUG] 2000-11-15 10:37:14.000 [SomeTag]: Some text. Very interesting and short text "
	UserName    = "Kirill"
	UserSurname = "Bogatikov"
	UserAge     = 20
	UserWeight  = 90 //don't joke about this please
)

func UserMock() User {
	return User{
		Name:    UserName,
		Surname: UserSurname,
		Age:     UserAge,
		Weight:  UserWeight,
	}
}

func TestDefaultFormatter_NoPayload(t *testing.T) {
	record := common.Record{
		Level:   common.DEBUG,
		Tag:     Tag,
		Message: Message,
		Time:    time.Date(2000, 11, 15, 10, 37, 14, 000, time.Local),
		Error:   nil,
		Data:    nil,
	}
	formatter := DefaultFormatter{}
	result := formatter.Format(record)
	assert.Equal(t, result, Base, "")
}

func TestDefaultFormatter_JsonPayload(t *testing.T) {
	user := UserMock()

	record := common.Record{
		Level:   common.DEBUG,
		Tag:     Tag,
		Message: Message,
		Time:    time.Date(2000, 11, 15, 10, 37, 14, 000, time.Local),
		Error:   nil,
		Data:    user,
	}

	formatter := DefaultFormatter{UseJson: true}
	result := formatter.Format(record)

	jsonBytes, _ := json.Marshal(user)
	jsonString := string(jsonBytes)
	assert.Equal(t, result, Base+jsonString, "")
}

func TestDefaultFormatter_FmtPayload(t *testing.T) {
	var user interface{} = UserMock()

	record := common.Record{
		Level:   common.DEBUG,
		Tag:     Tag,
		Message: Message,
		Time:    time.Date(2000, 11, 15, 10, 37, 14, 000, time.Local),
		Error:   nil,
		Data:    user,
	}

	formatter := DefaultFormatter{UseJson: false}
	result := formatter.Format(record)

	fmtString := fmt.Sprintf("\n%s", user)
	assert.Equal(t, result, Base+fmtString, "")
}
