# ZxingSDK  二维码扫描工具类

【支持功能】：
  1.生成二维码；
  2.扫描二维码；
  3.扫描图片二维码；
  4.扫描过程打开手电筒；
  

## 一.首先在项目的gradle中引用：

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


## 二.其次在dependencies中添加：
	dependencies {
	        compile 'com.github.lmiot:LmiotZxingSDK:1.1'
	}




## 三.扫描二维码，并返回结果：

     ZxingSdk.startScan(MainActivity.this, new ZxingSdk.onResultLitener() {
                    @Override
                    public void result(String result) {
                        Log.d("二维码扫描结果", result);
                    }
                });

## 四.生成二维码，并返回bitmap：

    ZxingSdk.CreateQRCode("https://www.baidu.com/", new ZxingSdk.onQRCodeLitener() {
                    @Override
                    public void result(Bitmap bitmap) {

                        if(bitmap!=null){
                            mImageView.setImageBitmap(bitmap);
                        }

                    }
                });



## 五.预览：
![](https://github.com/lmiot/LmiotZxingSDK/blob/master/img/sacn.gif)
![](https://github.com/lmiot/LmiotZxingSDK/blob/master/img/create.gif)