# ZxingSDK  二维码扫描工具类
  
  

## 一.首先在项目的gradle中引用：
<pre><code>
    allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' } //此处插入
          }
      }
</code></pre>


## 二.其次在dependencies中添加：
<pre><code>
dependencies {
        compile 'com.github.lmiot:ZxingSDK:1.1'
}
</code></pre>

## 三.预览：
![](https://github.com/lmiot/ZxingSDK/blob/master/img/zxing.gif)

## 四.扫描二维码：

     ZxingSdk.startScan(LoginActivity.this, new ZxingSdk.onResultLitener() {
               @Override
               public void result(String result) {
                   Log.d("ZxingSdk", "扫描结果："+result); //回调结果：String

               }
           });
## 五.生成二维码：

      ZxingSdk.QRCode("生成二维码的字符串"", new ZxingSdk.onQRCodeLitener() {
                                    @Override
                                    public void result(Bitmap bitmap) {
                                        zxingImg.setImageBitmap(bitmap); //回调结果：Bitmap

                                    }
                                });




