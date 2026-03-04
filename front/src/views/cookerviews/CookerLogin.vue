<!-- 厨师登录 -->
 <template>
 
 <div class="cookerLogin"> 
  <div class="cookerLogin-title">厨师登录</div>
  <div class="cookerLogin-input">
    <input type="text" placeholder="请输入用户名">
    <input type="password" placeholder="请输入密码">
  </div>
  <div class="cookerLogin-btn">
    <button>登录</button>
  </div>
 </div>

  
 </template>
 <script> 
 export default {
  name: "CookerLogin"
 }
 </script>
 <style>
 .cookerLogin{
   width: 100%;
   height: 100%;
   background-color: #f5f5f5;
 }

 .cookerLogin-title{
   width: 100%;
   height: 100px;
   line-height: 100px;
   text-align: center;
   font-size: 20px;
   font-weight: bold;
   color: #333;
 }

 .cookerLogin-input{
   width: 100%;
   height: 200px;
   display: flex;
   flex-direction: column;
   justify-content: center;
   align-items: center;
 }
 </style>