#支持的授权模式grant_type
authorization_code, password, refresh_token
##authorization_code模式：用于PC端，页面跳转，安全性最高，需要两步获取token;需确保redirect_uri和数据库中对应的redirect_uri一致
1. Get /oauth/authorize?client_id=SampleClientId&response_type=code&redirect_uri=http://client.sso.com/login/oauth2/code/sso-login
用户同意授权后服务端响应,浏览器重定向到：http://client.sso.com/login?code=1E37Xk，接收code,然后后端调用步骤2获取token
2. Post /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=authorization_code&redirect_uri=http://client.sso.com/login/oauth2/code/sso-login&code=1E37Xk
响应：
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "user_info",
    "userId": "1",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
##password模式：用于手机端或者其他无页面跳转场景，应由后台服务端调用，保护client_id和client_secret
Post /oauth/token?client_id=SampleClientId&client_secret=tgb.258&grant_type=password&scope=user_info&username=zhangsan&password=tgb.258
响应：
{
    "access_token": "a.b.c",
    "token_type": "bearer",
    "refresh_token": "d.e.f",
    "expires_in": 43199,
    "scope": "user_info",
    "userId": "1",
    "jti": "823cdd71-4732-4f9d-b949-a37ceb4488a4"
}
