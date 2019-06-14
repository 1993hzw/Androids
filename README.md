Androids
======
[![](https://jitpack.io/v/1993hzw/Androids.svg)](https://jitpack.io/#1993hzw/Androids)

Androids是本人根据平时的项目实践经验，为了提高Android开发效率而写的一个工具SDK；里面提供了一些工具类以及自定义View，可在实际项目开发时直接使用。

![ANDROIDS](https://raw.githubusercontent.com/1993hzw/common/master/Androids/androids4.png)

### 使用
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.1993hzw:Androids:1.2.4'
}
```
### 项目结构
#### --AndroidsDemo　使用例子

#### --androids 库工程

### 自定义View

  * #### [SView](https://raw.githubusercontent.com/1993hzw/Androids/README_SView.md)
  
  可直接在xml布局文件设置shape和selector，实现点击效果

  * #### [STextView](https://raw.githubusercontent.com/1993hzw/Androids/README_STextView.md)
  
  可直接在xml布局文件设置文字点击效果
  
  * #### [ShapeImageView](https://raw.githubusercontent.com/1993hzw/Androids/README_ShapeImageView.md)
  
  可设置形状(圆形、圆角矩形)的ImageView
 
  * #### [MaskImageView](https://raw.githubusercontent.com/1993hzw/Androids/README_MaskImageView.md)
  
  可很对背景图或前景图显示遮罩效果的ImageView

  * #### [RatioImageView](https://raw.githubusercontent.com/1993hzw/Androids/README_RatioImageView.md)
  
  可以设置宽高比例的ImageView
  
  * #### [ScrollPickerView](https://raw.githubusercontent.com/1993hzw/Androids/README_ScrollPickerView.md)
  
  滚动选择器，支持循环滚动，可实现生日选择器，老虎机等
    
  * #### [KeyboardLayout](https://raw.githubusercontent.com/1993hzw/Androids/README_KeyboardLayout.md)
  
  监听输入法键盘的弹起与隐藏，可实现输入法和工具栏无缝切换

  * #### [DragListView](https://raw.githubusercontent.com/1993hzw/Androids/README_DragListView.md)
  
  可拖拽的ListView，支持拖拽排序 
  
  * #### [EasyAdapter](https://raw.githubusercontent.com/1993hzw/Androids/README_EasyAdapter.md)
  
  用于RecyclerView的适配器，可支持设置点击、单选和多选模式

### 工具类

* #### 图片加载器
[点击查看示例代码](https://github.com/1993hzw/ImageSelector/blob/master/library/src/main/java/cn/hzw/imageselector/ImageLoader.java)

* #### AnimatorUtil

* #### 其他
  * [EllipsizeUtils](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/EllipsizeUtils.java)，文字截断和高亮，支持多行和根据关键字省略
  * [LogUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/LogUtil.java) 日志输出
  * [ProcessUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ProcessUtil.java) 进程相关
  * [ReflectUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ReflectUtil.java) 反射相关
  * [StatusBarUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/StatusBarUtil.java) 设置页面为沉浸式状态栏
  * [ThreadUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ThreadUtil.java) 线程相关，包括主线程和子线程
  * [DateUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/DateUtil.java) 日期相关
  * [Util](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/Util.java)

### 更新
* v1.2.4（18） 

1.增加工具类[EllipsizeUtils](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/EllipsizeUtils.java)，支持多行和根据关键字省略

* v1.2.3（17） 

1.fix:SelectorAttrs在低版本上没有设置背景色时显示黑色

* v1.2.2（16） 

1.优化手势识别类TouchGestureDetector,ScaleGestureDetector使用Api27源码

* v1.2（14） 

1.新增EasyAdapter,用于RecyclerView的适配器，可支持设置点击、单选和多选模式。（[Kotlin版实现](https://github.com/1993hzw/EasyAdapterForRecyclerView-kotlin)
）

2.修复SLayout没有设置背景色时导致border异常的问题

* v1.1.8.1（13） 

1.修复某些情况下滚动选择器不回调监听器的问题

* v1.1.8（12） 

1.SLayout支持水波纹效果

2.兼容旧版本的sel_background属性

* v1.1.7.1（11） 

1.修复SelectorAttrs中background_border的bug。

* v1.1.7（10） 

1.完善SelectorAttrs逻辑，支持设置图片。

2.增加TouchGestureDetector

* v1.1.6（9） 

文字滚动选择器支持文字换行.
