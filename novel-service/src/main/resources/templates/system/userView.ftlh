{
  <#if user??>
     "success": true
     "data": {
     "userId":"${user.userId}",
     "username":"${user.username}",
     "password":"${user.password}",
     "fullname":"${user.fullname}"
  </#if>
}