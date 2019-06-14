KeyboardLayout
======
监听输入法键盘的弹起与隐藏，可实现输入法和工具栏无缝切换

![01](https://raw.githubusercontent.com/1993hzw/common/master/Androids/keyboardlayout.gif)

* 示例

在布局中任意添加：
```xml
...

<cn.forward.androids.views.KeyboardLayout
        android:id="@+id/keyboard_layout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
...

```
```java
mKeyboardLayout = (KeyboardLayout) findViewById(R.id.keyboard_layout);
mKeyboardLayout.setKeyboardListener(new KeyboardLayout.KeyboardLayoutListener() {
        @Override
        public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
            if (isActive) { // 输入法打开
                // ...
            } else{
                // ...
            }
        }
    });
```

[点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/KeyboardLayoutDemo.java)

* 更多

[《Android监听输入法并获取高度——输入法与页面布局无缝切换》](https://juejin.im/post/5a7eb8005188257a642693b3)