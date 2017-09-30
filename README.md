# FaceDetectAnimation
仿手Q人脸识别动画

# 效果图
# ![image](https://github.com/SmallBlueWhale/FaceDetectAnimation/raw/master/GIF.gif "效果图")

# 用法
## 1. 添加依赖
克隆本项目添加依赖或者在Gradle中添加依赖:
```gradle
    compile 'com.huayuxun.whale.whaleradargraph.widget.WhaleShareView'
```
 > Gradle方式可能暂时无法使用

## 2. 布局文件中定义
```xml  
    <com.hyx.whale.facedetectanimation.FaceDetectView.FaceDetectView
        android:id="@+id/whaleLineChart"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
```

## 3. 添加数据
```java
        whaleShareView = (FaceDetectView) findViewById(R.id.faceDetectView);
        
```

# 更多特性
 -  xml属性  
 暂时未加入太多自定义属性



- 外部接口
FaceDetectView.setLinearMargin(float margin):可动态设置左右间距
FaceDetectView.setVerMargin(float margin):可动态设置上下间距

# License
    The MIT License (MIT)

    Copyright (c) 2017 whale(王金辉)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

