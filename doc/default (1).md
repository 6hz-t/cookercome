# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://localhost:8080


**联系人**:


**Version**:v0


**接口路径**:/v3/api-docs/default


[TOC]






# 厨师服务接口


## 厨师接单


**接口地址**:`/api/chef/acceptOrder`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>接单</p>



**请求示例**:


```javascript
{
  "orderId": 0,
  "chefId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefAcceptRequest|ChefAcceptRequest|body|true|ChefAcceptRequest|ChefAcceptRequest|
|&emsp;&emsp;orderId|||false|integer(int64)||
|&emsp;&emsp;chefId|||false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取历史订单


**接口地址**:`/api/chef/getHistoryOrders`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取历史订单</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefId||query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageOrder|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|Order|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;orderNo||string||
|&emsp;&emsp;customerId||integer(int64)||
|&emsp;&emsp;chefId||integer(int64)||
|&emsp;&emsp;addressId||integer(int64)||
|&emsp;&emsp;reserveDate||string(date-time)||
|&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;paymentTime||string(date-time)||
|&emsp;&emsp;serviceStartTime||string(date-time)||
|&emsp;&emsp;serviceEndTime||string(date-time)||
|&emsp;&emsp;remark||string||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;column||string||
|&emsp;&emsp;asc||boolean||
|optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;records||array|Order|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;customerId||integer||
|&emsp;&emsp;&emsp;&emsp;chefId||integer||
|&emsp;&emsp;&emsp;&emsp;addressId||integer||
|&emsp;&emsp;&emsp;&emsp;reserveDate||string||
|&emsp;&emsp;&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;paymentTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceStartTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceEndTime||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;searchCount||PageOrder|PageOrder|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageOrder|PageOrder|
|&emsp;&emsp;records||array|Order|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;customerId||integer||
|&emsp;&emsp;&emsp;&emsp;chefId||integer||
|&emsp;&emsp;&emsp;&emsp;addressId||integer||
|&emsp;&emsp;&emsp;&emsp;reserveDate||string||
|&emsp;&emsp;&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;paymentTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceStartTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceEndTime||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;searchCount||PageOrder|PageOrder|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|optimizeJoinOfCountSql||boolean||
|maxLimit||integer(int64)|integer(int64)|
|countId||string||
|pages||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"records": [
		{
			"id": 0,
			"orderNo": "",
			"customerId": 0,
			"chefId": 0,
			"addressId": 0,
			"reserveDate": "",
			"reserveTime": "",
			"dishRequirements": "",
			"totalAmount": 0,
			"status": 0,
			"paymentTime": "",
			"serviceStartTime": "",
			"serviceEndTime": "",
			"remark": "",
			"createTime": "",
			"updateTime": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"column": "",
			"asc": true
		}
	],
	"optimizeCountSql": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"searchCount": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"optimizeJoinOfCountSql": true,
	"maxLimit": 0,
	"countId": "",
	"pages": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 查询新订单


**接口地址**:`/api/chef/getNewOrders`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>查询新订单</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageOrder|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|Order|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;orderNo||string||
|&emsp;&emsp;customerId||integer(int64)||
|&emsp;&emsp;chefId||integer(int64)||
|&emsp;&emsp;addressId||integer(int64)||
|&emsp;&emsp;reserveDate||string(date-time)||
|&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;paymentTime||string(date-time)||
|&emsp;&emsp;serviceStartTime||string(date-time)||
|&emsp;&emsp;serviceEndTime||string(date-time)||
|&emsp;&emsp;remark||string||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;column||string||
|&emsp;&emsp;asc||boolean||
|optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;records||array|Order|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;customerId||integer||
|&emsp;&emsp;&emsp;&emsp;chefId||integer||
|&emsp;&emsp;&emsp;&emsp;addressId||integer||
|&emsp;&emsp;&emsp;&emsp;reserveDate||string||
|&emsp;&emsp;&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;paymentTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceStartTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceEndTime||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;searchCount||PageOrder|PageOrder|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageOrder|PageOrder|
|&emsp;&emsp;records||array|Order|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;customerId||integer||
|&emsp;&emsp;&emsp;&emsp;chefId||integer||
|&emsp;&emsp;&emsp;&emsp;addressId||integer||
|&emsp;&emsp;&emsp;&emsp;reserveDate||string||
|&emsp;&emsp;&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;paymentTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceStartTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceEndTime||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;searchCount||PageOrder|PageOrder|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|optimizeJoinOfCountSql||boolean||
|maxLimit||integer(int64)|integer(int64)|
|countId||string||
|pages||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"records": [
		{
			"id": 0,
			"orderNo": "",
			"customerId": 0,
			"chefId": 0,
			"addressId": 0,
			"reserveDate": "",
			"reserveTime": "",
			"dishRequirements": "",
			"totalAmount": 0,
			"status": 0,
			"paymentTime": "",
			"serviceStartTime": "",
			"serviceEndTime": "",
			"remark": "",
			"createTime": "",
			"updateTime": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"column": "",
			"asc": true
		}
	],
	"optimizeCountSql": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"searchCount": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"optimizeJoinOfCountSql": true,
	"maxLimit": 0,
	"countId": "",
	"pages": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取服务中订单


**接口地址**:`/api/chef/getOrders`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取服务中订单</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefId||query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|PageOrder|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|records||array|Order|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;orderNo||string||
|&emsp;&emsp;customerId||integer(int64)||
|&emsp;&emsp;chefId||integer(int64)||
|&emsp;&emsp;addressId||integer(int64)||
|&emsp;&emsp;reserveDate||string(date-time)||
|&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;status||integer(int32)||
|&emsp;&emsp;paymentTime||string(date-time)||
|&emsp;&emsp;serviceStartTime||string(date-time)||
|&emsp;&emsp;serviceEndTime||string(date-time)||
|&emsp;&emsp;remark||string||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|total||integer(int64)|integer(int64)|
|size||integer(int64)|integer(int64)|
|current||integer(int64)|integer(int64)|
|orders||array|OrderItem|
|&emsp;&emsp;column||string||
|&emsp;&emsp;asc||boolean||
|optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;records||array|Order|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;customerId||integer||
|&emsp;&emsp;&emsp;&emsp;chefId||integer||
|&emsp;&emsp;&emsp;&emsp;addressId||integer||
|&emsp;&emsp;&emsp;&emsp;reserveDate||string||
|&emsp;&emsp;&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;paymentTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceStartTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceEndTime||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;searchCount||PageOrder|PageOrder|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|searchCount||PageOrder|PageOrder|
|&emsp;&emsp;records||array|Order|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;orderNo||string||
|&emsp;&emsp;&emsp;&emsp;customerId||integer||
|&emsp;&emsp;&emsp;&emsp;chefId||integer||
|&emsp;&emsp;&emsp;&emsp;addressId||integer||
|&emsp;&emsp;&emsp;&emsp;reserveDate||string||
|&emsp;&emsp;&emsp;&emsp;reserveTime||string||
|&emsp;&emsp;&emsp;&emsp;dishRequirements||string||
|&emsp;&emsp;&emsp;&emsp;totalAmount||number||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;paymentTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceStartTime||string||
|&emsp;&emsp;&emsp;&emsp;serviceEndTime||string||
|&emsp;&emsp;&emsp;&emsp;remark||string||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;total||integer(int64)||
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;orders||array|OrderItem|
|&emsp;&emsp;&emsp;&emsp;column||string||
|&emsp;&emsp;&emsp;&emsp;asc||boolean||
|&emsp;&emsp;optimizeCountSql||PageOrder|PageOrder|
|&emsp;&emsp;searchCount||PageOrder|PageOrder|
|&emsp;&emsp;optimizeJoinOfCountSql||boolean||
|&emsp;&emsp;maxLimit||integer(int64)||
|&emsp;&emsp;countId||string||
|&emsp;&emsp;pages||integer(int64)||
|optimizeJoinOfCountSql||boolean||
|maxLimit||integer(int64)|integer(int64)|
|countId||string||
|pages||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"records": [
		{
			"id": 0,
			"orderNo": "",
			"customerId": 0,
			"chefId": 0,
			"addressId": 0,
			"reserveDate": "",
			"reserveTime": "",
			"dishRequirements": "",
			"totalAmount": 0,
			"status": 0,
			"paymentTime": "",
			"serviceStartTime": "",
			"serviceEndTime": "",
			"remark": "",
			"createTime": "",
			"updateTime": ""
		}
	],
	"total": 0,
	"size": 0,
	"current": 0,
	"orders": [
		{
			"column": "",
			"asc": true
		}
	],
	"optimizeCountSql": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"searchCount": {
		"records": [],
		"total": 0,
		"size": 0,
		"current": 0,
		"orders": [],
		"optimizeCountSql": "",
		"searchCount": "",
		"optimizeJoinOfCountSql": true,
		"maxLimit": 0,
		"countId": "",
		"pages": 0
	},
	"optimizeJoinOfCountSql": true,
	"maxLimit": 0,
	"countId": "",
	"pages": 0
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取今日已接订单


**接口地址**:`/api/chef/getTodayOrders`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取今日已接订单</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefId||query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultDeskInfoResponse|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||DeskInfoResponse|DeskInfoResponse|
|&emsp;&emsp;todayOrder||integer(int32)||
|&emsp;&emsp;waitOrder||integer(int32)||
|&emsp;&emsp;todayIncome||number(double)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"todayOrder": 0,
		"waitOrder": 0,
		"todayIncome": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新厨师信息


**接口地址**:`/api/chef/updateChefInfo`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新厨师信息</p>



**请求示例**:


```javascript
{
  "userId": 0,
  "relName": "",
  "phone": "",
  "idCardFrontUrl": "",
  "idCardBackUrl": "",
  "idCardNumber": "",
  "gender": 0,
  "address": {
    "fullAddress": "",
    "province": "",
    "city": "",
    "district": "",
    "street": "",
    "streetNumber": "",
    "longitude": 0,
    "latitude": 0
  },
  "introduction": "",
  "avatarUrl": "",
  "dishKinds": [],
  "qualificationUrl": "",
  "status": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|updateChefRequest|UpdateChefRequest|body|true|UpdateChefRequest|UpdateChefRequest|
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;relName|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;idCardFrontUrl|||false|string||
|&emsp;&emsp;idCardBackUrl|||false|string||
|&emsp;&emsp;idCardNumber|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;address|||false|Address|Address|
|&emsp;&emsp;&emsp;&emsp;fullAddress|||false|string||
|&emsp;&emsp;&emsp;&emsp;province|||false|string||
|&emsp;&emsp;&emsp;&emsp;city|||false|string||
|&emsp;&emsp;&emsp;&emsp;district|||false|string||
|&emsp;&emsp;&emsp;&emsp;street|||false|string||
|&emsp;&emsp;&emsp;&emsp;streetNumber|||false|string||
|&emsp;&emsp;&emsp;&emsp;longitude|||false|number||
|&emsp;&emsp;&emsp;&emsp;latitude|||false|number||
|&emsp;&emsp;introduction|||false|string||
|&emsp;&emsp;avatarUrl|||false|string||
|&emsp;&emsp;dishKinds|||false|array|string|
|&emsp;&emsp;qualificationUrl|||false|string||
|&emsp;&emsp;status|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 修改厨师状态


**接口地址**:`/api/chef/updateChefStatus`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>修改厨师状态</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefId||query|true|integer(int64)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新订单状态


**接口地址**:`/api/chef/updateOrderStatus`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>更新订单状态</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|orderId||query|true|integer(int64)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Result|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


# 厨师管理


## 创建厨师信息


**接口地址**:`/api/chef`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建新的厨师信息（需要管理员权限）</p>



**请求示例**:


```javascript
{
  "id": 0,
  "userId": 0,
  "realName": "",
  "gender": 0,
  "idCardNo": "",
  "phone": "",
  "idCardFront": "",
  "idCardBack": "",
  "detailAddress": "",
  "avatarUrl": "",
  "qualificationUrl": "",
  "experienceYears": 0,
  "introduction": "",
  "auditStatus": 0,
  "auditRemark": "",
  "latitude": 0,
  "longitude": 0,
  "createTime": "",
  "updateTime": "",
  "status": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefInfo|ChefInfo|body|true|ChefInfo|ChefInfo|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;realName|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;idCardNo|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;idCardFront|||false|string||
|&emsp;&emsp;idCardBack|||false|string||
|&emsp;&emsp;detailAddress|||false|string||
|&emsp;&emsp;avatarUrl|||false|string||
|&emsp;&emsp;qualificationUrl|||false|string||
|&emsp;&emsp;experienceYears|||false|integer(int32)||
|&emsp;&emsp;introduction|||false|string||
|&emsp;&emsp;auditStatus|||false|integer(int32)||
|&emsp;&emsp;auditRemark|||false|string||
|&emsp;&emsp;latitude|||false|number||
|&emsp;&emsp;longitude|||false|number||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;updateTime|||false|string(date-time)||
|&emsp;&emsp;status|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultChefInfo|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ChefInfo|ChefInfo|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;realName||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;idCardNo||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;idCardFront||string||
|&emsp;&emsp;idCardBack||string||
|&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;qualificationUrl||string||
|&emsp;&emsp;experienceYears||integer(int32)||
|&emsp;&emsp;introduction||string||
|&emsp;&emsp;auditStatus||integer(int32)||
|&emsp;&emsp;auditRemark||string||
|&emsp;&emsp;latitude||number||
|&emsp;&emsp;longitude||number||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|&emsp;&emsp;status||integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"userId": 0,
		"realName": "",
		"gender": 0,
		"idCardNo": "",
		"phone": "",
		"idCardFront": "",
		"idCardBack": "",
		"detailAddress": "",
		"avatarUrl": "",
		"qualificationUrl": "",
		"experienceYears": 0,
		"introduction": "",
		"auditStatus": 0,
		"auditRemark": "",
		"latitude": 0,
		"longitude": 0,
		"createTime": "",
		"updateTime": "",
		"status": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新厨师信息


**接口地址**:`/api/chef`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新指定厨师的信息</p>



**请求示例**:


```javascript
{
  "id": 0,
  "userId": 0,
  "realName": "",
  "gender": 0,
  "idCardNo": "",
  "phone": "",
  "idCardFront": "",
  "idCardBack": "",
  "detailAddress": "",
  "avatarUrl": "",
  "qualificationUrl": "",
  "experienceYears": 0,
  "introduction": "",
  "auditStatus": 0,
  "auditRemark": "",
  "latitude": 0,
  "longitude": 0,
  "createTime": "",
  "updateTime": "",
  "status": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|chefInfo|ChefInfo|body|true|ChefInfo|ChefInfo|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;realName|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;idCardNo|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;idCardFront|||false|string||
|&emsp;&emsp;idCardBack|||false|string||
|&emsp;&emsp;detailAddress|||false|string||
|&emsp;&emsp;avatarUrl|||false|string||
|&emsp;&emsp;qualificationUrl|||false|string||
|&emsp;&emsp;experienceYears|||false|integer(int32)||
|&emsp;&emsp;introduction|||false|string||
|&emsp;&emsp;auditStatus|||false|integer(int32)||
|&emsp;&emsp;auditRemark|||false|string||
|&emsp;&emsp;latitude|||false|number||
|&emsp;&emsp;longitude|||false|number||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;updateTime|||false|string(date-time)||
|&emsp;&emsp;status|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 审核厨师


**接口地址**:`/api/chef/audit/{id}`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>管理员审核厨师资质，可设置通过或拒绝</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|status||query|true|integer(int32)||
|reason||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取厨师详情


**接口地址**:`/api/chef/detail/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据厨师 ID 获取厨师详细信息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultChefInfo|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||ChefInfo|ChefInfo|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;realName||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;idCardNo||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;idCardFront||string||
|&emsp;&emsp;idCardBack||string||
|&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;qualificationUrl||string||
|&emsp;&emsp;experienceYears||integer(int32)||
|&emsp;&emsp;introduction||string||
|&emsp;&emsp;auditStatus||integer(int32)||
|&emsp;&emsp;auditRemark||string||
|&emsp;&emsp;latitude||number||
|&emsp;&emsp;longitude||number||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|&emsp;&emsp;status||integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"userId": 0,
		"realName": "",
		"gender": 0,
		"idCardNo": "",
		"phone": "",
		"idCardFront": "",
		"idCardBack": "",
		"detailAddress": "",
		"avatarUrl": "",
		"qualificationUrl": "",
		"experienceYears": 0,
		"introduction": "",
		"auditStatus": 0,
		"auditRemark": "",
		"latitude": 0,
		"longitude": 0,
		"createTime": "",
		"updateTime": "",
		"status": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取厨师列表


**接口地址**:`/api/chef/list`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取厨师列表信息（测试接口）</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 搜索附近厨师


**接口地址**:`/api/chef/nearby`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据经纬度搜索指定半径范围内的厨师</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|longitude||query|true|number(double)||
|latitude||query|true|number(double)||
|radius||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListChefInfo|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|ChefInfo|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;realName||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;idCardNo||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;idCardFront||string||
|&emsp;&emsp;idCardBack||string||
|&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;qualificationUrl||string||
|&emsp;&emsp;experienceYears||integer(int32)||
|&emsp;&emsp;introduction||string||
|&emsp;&emsp;auditStatus||integer(int32)||
|&emsp;&emsp;auditRemark||string||
|&emsp;&emsp;latitude||number||
|&emsp;&emsp;longitude||number||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|&emsp;&emsp;status||integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"userId": 0,
			"realName": "",
			"gender": 0,
			"idCardNo": "",
			"phone": "",
			"idCardFront": "",
			"idCardBack": "",
			"detailAddress": "",
			"avatarUrl": "",
			"qualificationUrl": "",
			"experienceYears": 0,
			"introduction": "",
			"auditStatus": 0,
			"auditRemark": "",
			"latitude": 0,
			"longitude": 0,
			"createTime": "",
			"updateTime": "",
			"status": 0
		}
	]
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新厨师资料


**接口地址**:`/api/chef/updateProfile`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>厨师更新个人资料信息</p>



**请求示例**:


```javascript
{
  "userId": 0,
  "relName": "",
  "phone": "",
  "idCardFrontUrl": "",
  "idCardBackUrl": "",
  "idCardNumber": "",
  "gender": 0,
  "address": {
    "fullAddress": "",
    "province": "",
    "city": "",
    "district": "",
    "street": "",
    "streetNumber": "",
    "longitude": 0,
    "latitude": 0
  },
  "introduction": "",
  "avatarUrl": "",
  "dishKinds": [],
  "qualificationUrl": "",
  "status": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|updateChefRequest|UpdateChefRequest|body|true|UpdateChefRequest|UpdateChefRequest|
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;relName|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;idCardFrontUrl|||false|string||
|&emsp;&emsp;idCardBackUrl|||false|string||
|&emsp;&emsp;idCardNumber|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;address|||false|Address|Address|
|&emsp;&emsp;&emsp;&emsp;fullAddress|||false|string||
|&emsp;&emsp;&emsp;&emsp;province|||false|string||
|&emsp;&emsp;&emsp;&emsp;city|||false|string||
|&emsp;&emsp;&emsp;&emsp;district|||false|string||
|&emsp;&emsp;&emsp;&emsp;street|||false|string||
|&emsp;&emsp;&emsp;&emsp;streetNumber|||false|string||
|&emsp;&emsp;&emsp;&emsp;longitude|||false|number||
|&emsp;&emsp;&emsp;&emsp;latitude|||false|number||
|&emsp;&emsp;introduction|||false|string||
|&emsp;&emsp;avatarUrl|||false|string||
|&emsp;&emsp;dishKinds|||false|array|string|
|&emsp;&emsp;qualificationUrl|||false|string||
|&emsp;&emsp;status|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


# 客户管理


## 获取收货地址列表


**接口地址**:`/api/customer/addresses`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前登录客户的所有收货地址</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListUserAddress|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|UserAddress|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;receiver||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;province||string||
|&emsp;&emsp;city||string||
|&emsp;&emsp;district||string||
|&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;latitude||number(double)||
|&emsp;&emsp;longitude||number(double)||
|&emsp;&emsp;isDefault||integer(int32)||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"userId": 0,
			"receiver": "",
			"phone": "",
			"province": "",
			"city": "",
			"district": "",
			"detailAddress": "",
			"latitude": 0,
			"longitude": 0,
			"isDefault": 0,
			"createTime": "",
			"updateTime": ""
		}
	]
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 添加收货地址


**接口地址**:`/api/customer/addresses`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>为当前登录客户添加新的收货地址</p>



**请求示例**:


```javascript
{
  "id": 0,
  "userId": 0,
  "receiver": "",
  "phone": "",
  "province": "",
  "city": "",
  "district": "",
  "detailAddress": "",
  "latitude": 0,
  "longitude": 0,
  "isDefault": 0,
  "createTime": "",
  "updateTime": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userAddress|UserAddress|body|true|UserAddress|UserAddress|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;receiver|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;province|||false|string||
|&emsp;&emsp;city|||false|string||
|&emsp;&emsp;district|||false|string||
|&emsp;&emsp;detailAddress|||false|string||
|&emsp;&emsp;latitude|||false|number(double)||
|&emsp;&emsp;longitude|||false|number(double)||
|&emsp;&emsp;isDefault|||false|integer(int32)||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;updateTime|||false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新收货地址


**接口地址**:`/api/customer/addresses/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新指定 ID 的收货地址信息</p>



**请求示例**:


```javascript
{
  "id": 0,
  "userId": 0,
  "receiver": "",
  "phone": "",
  "province": "",
  "city": "",
  "district": "",
  "detailAddress": "",
  "latitude": 0,
  "longitude": 0,
  "isDefault": 0,
  "createTime": "",
  "updateTime": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||
|userAddress|UserAddress|body|true|UserAddress|UserAddress|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;receiver|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;province|||false|string||
|&emsp;&emsp;city|||false|string||
|&emsp;&emsp;district|||false|string||
|&emsp;&emsp;detailAddress|||false|string||
|&emsp;&emsp;latitude|||false|number(double)||
|&emsp;&emsp;longitude|||false|number(double)||
|&emsp;&emsp;isDefault|||false|integer(int32)||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;updateTime|||false|string(date-time)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 删除收货地址


**接口地址**:`/api/customer/addresses/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>删除指定 ID 的收货地址</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取客户个人信息


**接口地址**:`/api/customer/profile`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前登录客户的个人信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultCustomerInfo|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||CustomerInfo|CustomerInfo|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;username||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;avatar||string||
|&emsp;&emsp;realName||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;email||string||
|&emsp;&emsp;birthday||string(date)||
|&emsp;&emsp;memberLevel||integer(int32)||
|&emsp;&emsp;points||integer(int32)||
|&emsp;&emsp;totalOrders||integer(int32)||
|&emsp;&emsp;completedOrders||integer(int32)||
|&emsp;&emsp;averageRating||number||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|&emsp;&emsp;status||integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"userId": 0,
		"username": "",
		"phone": "",
		"avatar": "",
		"realName": "",
		"gender": 0,
		"email": "",
		"birthday": "",
		"memberLevel": 0,
		"points": 0,
		"totalOrders": 0,
		"completedOrders": 0,
		"averageRating": 0,
		"createTime": "",
		"updateTime": "",
		"status": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 更新客户个人信息


**接口地址**:`/api/customer/profile`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新当前登录客户的个人信息</p>



**请求示例**:


```javascript
{
  "id": 0,
  "userId": 0,
  "username": "",
  "phone": "",
  "avatar": "",
  "realName": "",
  "gender": 0,
  "email": "",
  "birthday": "",
  "memberLevel": 0,
  "points": 0,
  "totalOrders": 0,
  "completedOrders": 0,
  "averageRating": 0,
  "createTime": "",
  "updateTime": "",
  "status": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|customerInfo|CustomerInfo|body|true|CustomerInfo|CustomerInfo|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;userId|||false|integer(int64)||
|&emsp;&emsp;username|||false|string||
|&emsp;&emsp;phone|||false|string||
|&emsp;&emsp;avatar|||false|string||
|&emsp;&emsp;realName|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;email|||false|string||
|&emsp;&emsp;birthday|||false|string(date)||
|&emsp;&emsp;memberLevel|||false|integer(int32)||
|&emsp;&emsp;points|||false|integer(int32)||
|&emsp;&emsp;totalOrders|||false|integer(int32)||
|&emsp;&emsp;completedOrders|||false|integer(int32)||
|&emsp;&emsp;averageRating|||false|number||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;updateTime|||false|string(date-time)||
|&emsp;&emsp;status|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 获取个人中心统计信息


**接口地址**:`/api/customer/stats`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前登录用户的订单、收藏、优惠券等统计信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringObject|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


# 认证管理


## 获取当前用户信息


**接口地址**:`/api/auth/current`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取当前登录用户的详细信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUser|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||User|User|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;password||string||
|&emsp;&emsp;role||integer(int32)||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"phone": "",
		"password": "",
		"role": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 用户登录


**接口地址**:`/api/auth/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>用户通过手机号和密码登录，返回 Access Token 和 Refresh Token</p>



**请求示例**:


```javascript
{
  "phone": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginRequest|LoginRequest|body|true|LoginRequest|LoginRequest|
|&emsp;&emsp;phone|||true|string||
|&emsp;&emsp;password|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultAuthResponse|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||AuthResponse|AuthResponse|
|&emsp;&emsp;accessToken||string||
|&emsp;&emsp;refreshToken||string||
|&emsp;&emsp;expiresIn||integer(int64)||
|&emsp;&emsp;userInfo||UserInfoVO|UserInfoVO|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;role||integer||
|&emsp;&emsp;customerInfo||CustomerInfoVO|CustomerInfoVO|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;birthday||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"accessToken": "",
		"refreshToken": "",
		"expiresIn": 0,
		"userInfo": {
			"id": 0,
			"phone": "",
			"role": 0
		},
		"customerInfo": {
			"id": 0,
			"userId": 0,
			"username": "",
			"avatar": "",
			"realName": "",
			"gender": 0,
			"email": "",
			"birthday": ""
		}
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 退出登录


**接口地址**:`/api/auth/logout`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>用户退出登录，使 Token 失效</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 刷新 Token


**接口地址**:`/api/auth/refresh`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>使用 Refresh Token 获取新的 Access Token 和 Refresh Token</p>



**请求示例**:


```javascript
{
  "refreshToken": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|refreshTokenRequest|RefreshTokenRequest|body|true|RefreshTokenRequest|RefreshTokenRequest|
|&emsp;&emsp;refreshToken|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultAuthResponse|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||AuthResponse|AuthResponse|
|&emsp;&emsp;accessToken||string||
|&emsp;&emsp;refreshToken||string||
|&emsp;&emsp;expiresIn||integer(int64)||
|&emsp;&emsp;userInfo||UserInfoVO|UserInfoVO|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;role||integer||
|&emsp;&emsp;customerInfo||CustomerInfoVO|CustomerInfoVO|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;birthday||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"accessToken": "",
		"refreshToken": "",
		"expiresIn": 0,
		"userInfo": {
			"id": 0,
			"phone": "",
			"role": 0
		},
		"customerInfo": {
			"id": 0,
			"userId": 0,
			"username": "",
			"avatar": "",
			"realName": "",
			"gender": 0,
			"email": "",
			"birthday": ""
		}
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## 用户注册


**接口地址**:`/api/auth/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>新用户通过手机号和密码进行注册</p>



**请求示例**:


```javascript
{
  "phone": "",
  "password": "",
  "role": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|registerRequest|RegisterRequest|body|true|RegisterRequest|RegisterRequest|
|&emsp;&emsp;phone|||true|string||
|&emsp;&emsp;password|||true|string||
|&emsp;&emsp;role|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUser|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||User|User|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;password||string||
|&emsp;&emsp;role||integer(int32)||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"phone": "",
		"password": "",
		"role": 0,
		"createTime": "",
		"updateTime": ""
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


# 用户管理


## getUserDetail


**接口地址**:`/user-manage/detail/{userId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultUserDetailVO|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||UserDetailVO|UserDetailVO|
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;role||integer(int32)||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;status||string||
|&emsp;&emsp;name||string||
|&emsp;&emsp;adminInfo||AdminInfo|AdminInfo|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;birthday||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;chefInfo||ChefInfo|ChefInfo|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;idCardNo||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;idCardFront||string||
|&emsp;&emsp;&emsp;&emsp;idCardBack||string||
|&emsp;&emsp;&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;&emsp;&emsp;qualificationUrl||string||
|&emsp;&emsp;&emsp;&emsp;experienceYears||integer||
|&emsp;&emsp;&emsp;&emsp;introduction||string||
|&emsp;&emsp;&emsp;&emsp;auditStatus||integer||
|&emsp;&emsp;&emsp;&emsp;auditRemark||string||
|&emsp;&emsp;&emsp;&emsp;latitude||number||
|&emsp;&emsp;&emsp;&emsp;longitude||number||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;customerInfo||CustomerInfo|CustomerInfo|
|&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;birthday||string||
|&emsp;&emsp;&emsp;&emsp;memberLevel||integer||
|&emsp;&emsp;&emsp;&emsp;points||integer||
|&emsp;&emsp;&emsp;&emsp;totalOrders||integer||
|&emsp;&emsp;&emsp;&emsp;completedOrders||integer||
|&emsp;&emsp;&emsp;&emsp;averageRating||number||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;status||integer||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"userId": 0,
		"phone": "",
		"role": 0,
		"createTime": "",
		"status": "",
		"name": "",
		"adminInfo": {
			"id": 0,
			"userId": 0,
			"username": "",
			"avatar": "",
			"realName": "",
			"gender": 0,
			"email": "",
			"birthday": "",
			"status": 0,
			"createTime": "",
			"updateTime": ""
		},
		"chefInfo": {
			"id": 0,
			"userId": 0,
			"realName": "",
			"gender": 0,
			"idCardNo": "",
			"phone": "",
			"idCardFront": "",
			"idCardBack": "",
			"detailAddress": "",
			"avatarUrl": "",
			"qualificationUrl": "",
			"experienceYears": 0,
			"introduction": "",
			"auditStatus": 0,
			"auditRemark": "",
			"latitude": 0,
			"longitude": 0,
			"createTime": "",
			"updateTime": "",
			"status": 0
		},
		"customerInfo": {
			"id": 0,
			"userId": 0,
			"username": "",
			"phone": "",
			"avatar": "",
			"realName": "",
			"gender": 0,
			"email": "",
			"birthday": "",
			"memberLevel": 0,
			"points": 0,
			"totalOrders": 0,
			"completedOrders": 0,
			"averageRating": 0,
			"createTime": "",
			"updateTime": "",
			"status": 0
		}
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## getUserList


**接口地址**:`/user-manage/list`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|pageNum||query|false|integer(int32)||
|pageSize||query|false|integer(int32)||
|keyword||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultIPageUserDetailVO|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||IPageUserDetailVO|IPageUserDetailVO|
|&emsp;&emsp;size||integer(int64)||
|&emsp;&emsp;records||array|UserDetailVO|
|&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;role||integer||
|&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;status||string||
|&emsp;&emsp;&emsp;&emsp;name||string||
|&emsp;&emsp;&emsp;&emsp;adminInfo||AdminInfo|AdminInfo|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;birthday||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;chefInfo||ChefInfo|ChefInfo|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;idCardNo||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;idCardFront||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;idCardBack||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;avatarUrl||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;qualificationUrl||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;experienceYears||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;introduction||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;auditStatus||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;auditRemark||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;latitude||number||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;longitude||number||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;&emsp;&emsp;customerInfo||CustomerInfo|CustomerInfo|
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;id||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;userId||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;username||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;phone||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;avatar||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;realName||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;gender||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;email||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;birthday||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;memberLevel||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;points||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;totalOrders||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;completedOrders||integer||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;averageRating||number||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;createTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;updateTime||string||
|&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;status||integer||
|&emsp;&emsp;current||integer(int64)||
|&emsp;&emsp;pages||integer(int64)||
|&emsp;&emsp;total||integer(int64)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"size": 0,
		"records": [
			{
				"userId": 0,
				"phone": "",
				"role": 0,
				"createTime": "",
				"status": "",
				"name": "",
				"adminInfo": {
					"id": 0,
					"userId": 0,
					"username": "",
					"avatar": "",
					"realName": "",
					"gender": 0,
					"email": "",
					"birthday": "",
					"status": 0,
					"createTime": "",
					"updateTime": ""
				},
				"chefInfo": {
					"id": 0,
					"userId": 0,
					"realName": "",
					"gender": 0,
					"idCardNo": "",
					"phone": "",
					"idCardFront": "",
					"idCardBack": "",
					"detailAddress": "",
					"avatarUrl": "",
					"qualificationUrl": "",
					"experienceYears": 0,
					"introduction": "",
					"auditStatus": 0,
					"auditRemark": "",
					"latitude": 0,
					"longitude": 0,
					"createTime": "",
					"updateTime": "",
					"status": 0
				},
				"customerInfo": {
					"id": 0,
					"userId": 0,
					"username": "",
					"phone": "",
					"avatar": "",
					"realName": "",
					"gender": 0,
					"email": "",
					"birthday": "",
					"memberLevel": 0,
					"points": 0,
					"totalOrders": 0,
					"completedOrders": 0,
					"averageRating": 0,
					"createTime": "",
					"updateTime": "",
					"status": 0
				}
			}
		],
		"current": 0,
		"pages": 0,
		"total": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## resetPassword


**接口地址**:`/user-manage/reset-password/{userId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultBoolean|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## changeUserStatus


**接口地址**:`/user-manage/status/{userId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId||path|true|integer(int64)||
|status||query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultBoolean|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||boolean||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": true
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


# customer-settings-controller


## addAddress


**接口地址**:`/api/customer/settings/address/add`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "receiver": "",
  "phone": "",
  "province": "",
  "city": "",
  "district": "",
  "detailAddress": "",
  "latitude": 0,
  "longitude": 0,
  "isDefault": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|customerAddressRequest|CustomerAddressRequest|body|true|CustomerAddressRequest|CustomerAddressRequest|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;receiver|||true|string||
|&emsp;&emsp;phone|||true|string||
|&emsp;&emsp;province|||true|string||
|&emsp;&emsp;city|||true|string||
|&emsp;&emsp;district|||true|string||
|&emsp;&emsp;detailAddress|||true|string||
|&emsp;&emsp;latitude|||false|number(double)||
|&emsp;&emsp;longitude|||false|number(double)||
|&emsp;&emsp;isDefault|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## deleteAddress


**接口地址**:`/api/customer/settings/address/delete/{addressId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|addressId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## getAddressList


**接口地址**:`/api/customer/settings/address/list`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultListUserAddress|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||array|UserAddress|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;receiver||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;province||string||
|&emsp;&emsp;city||string||
|&emsp;&emsp;district||string||
|&emsp;&emsp;detailAddress||string||
|&emsp;&emsp;latitude||number(double)||
|&emsp;&emsp;longitude||number(double)||
|&emsp;&emsp;isDefault||integer(int32)||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": [
		{
			"id": 0,
			"userId": 0,
			"receiver": "",
			"phone": "",
			"province": "",
			"city": "",
			"district": "",
			"detailAddress": "",
			"latitude": 0,
			"longitude": 0,
			"isDefault": 0,
			"createTime": "",
			"updateTime": ""
		}
	]
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## setDefaultAddress


**接口地址**:`/api/customer/settings/address/set-default/{addressId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|addressId||path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## updateAddress


**接口地址**:`/api/customer/settings/address/update`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "id": 0,
  "receiver": "",
  "phone": "",
  "province": "",
  "city": "",
  "district": "",
  "detailAddress": "",
  "latitude": 0,
  "longitude": 0,
  "isDefault": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|customerAddressRequest|CustomerAddressRequest|body|true|CustomerAddressRequest|CustomerAddressRequest|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;receiver|||true|string||
|&emsp;&emsp;phone|||true|string||
|&emsp;&emsp;province|||true|string||
|&emsp;&emsp;city|||true|string||
|&emsp;&emsp;district|||true|string||
|&emsp;&emsp;detailAddress|||true|string||
|&emsp;&emsp;latitude|||false|number(double)||
|&emsp;&emsp;longitude|||false|number(double)||
|&emsp;&emsp;isDefault|||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## uploadAvatar


**接口地址**:`/api/customer/settings/avatar`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|file||query|true|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## changePassword


**接口地址**:`/api/customer/settings/password/change`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "oldPassword": "",
  "newPassword": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|customerPasswordChangeRequest|CustomerPasswordChangeRequest|body|true|CustomerPasswordChangeRequest|CustomerPasswordChangeRequest|
|&emsp;&emsp;oldPassword|||true|string||
|&emsp;&emsp;newPassword|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## bindPhone


**接口地址**:`/api/customer/settings/phone/bind`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "newPhone": "",
  "currentPassword": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|customerPhoneBindRequest|CustomerPhoneBindRequest|body|true|CustomerPhoneBindRequest|CustomerPhoneBindRequest|
|&emsp;&emsp;newPhone|||true|string||
|&emsp;&emsp;currentPassword|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## getProfile


**接口地址**:`/api/customer/settings/profile`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultCustomerInfo|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||CustomerInfo|CustomerInfo|
|&emsp;&emsp;id||integer(int64)||
|&emsp;&emsp;userId||integer(int64)||
|&emsp;&emsp;username||string||
|&emsp;&emsp;phone||string||
|&emsp;&emsp;avatar||string||
|&emsp;&emsp;realName||string||
|&emsp;&emsp;gender||integer(int32)||
|&emsp;&emsp;email||string||
|&emsp;&emsp;birthday||string(date)||
|&emsp;&emsp;memberLevel||integer(int32)||
|&emsp;&emsp;points||integer(int32)||
|&emsp;&emsp;totalOrders||integer(int32)||
|&emsp;&emsp;completedOrders||integer(int32)||
|&emsp;&emsp;averageRating||number||
|&emsp;&emsp;createTime||string(date-time)||
|&emsp;&emsp;updateTime||string(date-time)||
|&emsp;&emsp;status||integer(int32)||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"id": 0,
		"userId": 0,
		"username": "",
		"phone": "",
		"avatar": "",
		"realName": "",
		"gender": 0,
		"email": "",
		"birthday": "",
		"memberLevel": 0,
		"points": 0,
		"totalOrders": 0,
		"completedOrders": 0,
		"averageRating": 0,
		"createTime": "",
		"updateTime": "",
		"status": 0
	}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


## updateProfile


**接口地址**:`/api/customer/settings/profile`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "avatar": "",
  "username": "",
  "realName": "",
  "gender": 0,
  "email": "",
  "birthday": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|settingsProfileUpdateRequest|SettingsProfileUpdateRequest|body|true|SettingsProfileUpdateRequest|SettingsProfileUpdateRequest|
|&emsp;&emsp;avatar|||false|string||
|&emsp;&emsp;username|||true|string||
|&emsp;&emsp;realName|||false|string||
|&emsp;&emsp;gender|||false|integer(int32)||
|&emsp;&emsp;email|||false|string||
|&emsp;&emsp;birthday|||false|string(date)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


# oss-signature-controller


## getSignatureUrl


**接口地址**:`/api/oss/signature`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|objectKey||query|true|string||
|expireMinutes||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ResultMapStringString|
|400|Bad Request|ResultVoid|
|403|Forbidden|ResultVoid|
|404|Not Found|ResultVoid|
|405|Method Not Allowed|ResultVoid|
|500|Internal Server Error|ResultVoid|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-405**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||object||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {}
}
```