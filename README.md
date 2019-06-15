Androids
======
[![](https://jitpack.io/v/1993hzw/Androids.svg)](https://jitpack.io/#1993hzw/Androids)

Androids是本人根据平时的项目实践经验，为了提高Android开发效率而写的一个工具SDK；里面提供了一些工具类以及自定义View，可在实际项目开发时直接使用。

![ANDROIDS](https://raw.githubusercontent.com/1993hzw/common/master/Androids/android4.png)

### 使用
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.1993hzw:Androids:1.3'
}
```
### 自定义View

  * #### [SView](https://github.com/1993hzw/Androids/blob/master/README_SView.md)
  
  可直接在xml布局文件设置shape和selector，实现点击效果

  * #### [STextView](https://github.com/1993hzw/Androids/blob/master/README_STextView.md)
  
  可直接在xml布局文件设置文字点击效果
  
  * #### [ShapeImageView](https://github.com/1993hzw/Androids/blob/master/README_ShapeImageView.md)
  
  可设置形状(圆形、圆角矩形)的ImageView
 
  * #### [MaskImageView](https://github.com/1993hzw/Androids/blob/master/README_MaskImageView.md)
  
  可很对背景图或前景图显示遮罩效果的ImageView

  * #### [RatioImageView](https://github.com/1993hzw/Androids/blob/master/README_RatioImageView.md)
  
  可以设置宽高比例的ImageView
  
  * #### [PaddingView](https://github.com/1993hzw/Androids/blob/master/README_PaddingView.md)
    
  在xml布局文件设置View的内容padding, 支持靠边对齐。用于解决点击区域大于内容区域的问题
  
  * #### [ScrollPickerView](https://github.com/1993hzw/Androids/blob/master/README_ScrollPickerView.md)
  
  滚动选择器，支持循环滚动，可实现生日选择器，老虎机等
    
  * #### [KeyboardLayout](https://github.com/1993hzw/Androids/blob/master/README_KeyboardLayout.md)
  
  监听输入法键盘的弹起与隐藏，可实现输入法和工具栏无缝切换

  * #### [DragListView](https://github.com/1993hzw/Androids/blob/master/README_DragListView.md)
  
  可拖拽的ListView，支持拖拽排序 
  
  * #### [EasyAdapter](https://github.com/1993hzw/Androids/blob/master/README_EasyAdapter.md)
  
  用于RecyclerView的适配器，可支持设置点击、单选和多选模式

### 工具类

  * [EllipsizeUtils](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/EllipsizeUtils.java)，文字截断和高亮，支持多行和根据关键字省略
  * [LogUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/LogUtil.java) 日志输出
  * [ProcessUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ProcessUtil.java) 进程相关
  * [ReflectUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ReflectUtil.java) 反射相关
  * [StatusBarUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/StatusBarUtil.java) 设置页面为沉浸式状态栏
  * [ThreadUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ThreadUtil.java) 线程相关，包括主线程和子线程
  * [DateUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/DateUtil.java) 日期相关
  * [AnimatorUtil](https://github.com/1993hzw/Androids/blob/master/README_AnimatorUtil.md) 对AnimatorSet进行封装，便以链式构建动画；支持设置循环次数
  * [Util](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/Util.java)
  * [ImageLoader](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/Image/LocalImagerLoader.java) 简单的图片加载器

