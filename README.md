# UseBlessingSkin
**支持了最新的 SkinsRestorer 15.0.x**  
让 SkinsRestorer 使用 BlessingSkin 搭建的皮肤站上的皮肤!   
支持 CSL API 也可以使用本插件  
支持 BungeeCord 和 Bukkit

## 使用方法
 1. 下载最新的 Releases 版本 (Actions 里有开发版)
 2. 扔进 Plugins 文件夹
 3. 使用 plugman 加载或重启服务器

## 权限
 - UseBlessingSkin.admin
   - 管理员权限

## 指令
 - /bskin
 - /bskin set 设置皮肤
 - /bskin help 帮助
 - /bskin reload 重载插件 (需要管理员权限)

## 配置文件
 - csl
   - 获取角色信息链接
   - 格式: https://example.com/csl/%name%.json
   - example.com 换成皮肤站的域名
   - 若更改了 csl 默认地址 请进行替换
   - **注意: %name%.json 必须保留**
   
 - texture
   - 获取材质链接
   - 格式: https://example.com/textures/%textureId%
   - example.com 换成皮肤站的域名
   - 若更改了材质默认地址 请进行替换
   - **注意: %textureId% 必须保留**
  
 - name
   - 皮肤站名称
 
 - url
   - 皮肤站地址
   
 - mineskinapi
   - MineSkinApi 地址
   - 若您自建了 MineSkinApi 可以进行替换
 
 - cdn
   - 皮肤站是否启用 CDN
   - 用于控制 IfCdnMakeRoleSkinNotExist 输出
 
 - cache
   - 材质是否缓存
 
 - message
   - 插件文本
   - Support: 输出帮助时显示 用于引出支持的皮肤站
   - SetSkin: 输出帮助时显示 表示设置皮肤
   - AboutIdInfo: 输出帮助时显示 关于帮助中 \<ID\> 的解释
   - RequestError: 请求错误
   - RoleNotExist: 角色不存在，请检查后重试
   - RoleResponseEmpty: 皮肤站响应为空
   - RoleSkinNotExist: 角色无皮肤
   - IfCdnMakeRoleSkinNotExist: 角色无皮肤时且使用CDN输出 关于CDN缓存的说明
   - TextureIdGetSuccess: 材质ID获取成功
   - SaveTextureError: 保存材质时出现异常
   - SaveTextureSuccess: 保存材质成功
   - UploadingTexture: 正在上传材质至MineSkin
   - UploadTextureError: 上传材质至MineSkin时出现异常
   - UploadTextureSuccess: 上传材质至MineSkin成功
   - SetSkinSuccess: 材质设置成功
   - UnknownError: 出现了未知错误
   - DoNotHavePermission: 没有权限执行指令
   - ReloadSuccess: 插件重载成功
   
## bStats
[Bukkit](https://bstats.org/plugin/bukkit/UseBlessingSkin/7957)  
[BungeeCord](https://bstats.org/plugin/bungeecord/UseBlessingSkin/7959)  

## License
MIT License