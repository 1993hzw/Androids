package cn.forward.androids.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;

import java.util.WeakHashMap;

import cn.forward.androids.R;
import cn.forward.androids.annotation.ViewInjectProcessor;

/**
 * Created by huangziwei on 16-11-4.
 */
public class InjectionLayoutInflater extends LayoutInflater implements LayoutInflater.Factory {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    private final static WeakHashMap<Context, InjectionLayoutInflater> WEAK_HASH_MAP = new WeakHashMap<Context, InjectionLayoutInflater>();

    private OnViewCreatedListener mOnViewCreatedListener;

    protected InjectionLayoutInflater(Context context) {
        super(context);
        setFactory(this);
    }

    protected InjectionLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
        setFactory(this);
    }

    public static InjectionLayoutInflater from(Context context) {
        InjectionLayoutInflater layoutInflater = WEAK_HASH_MAP.get(context);
        if (layoutInflater == null) {
            layoutInflater = new InjectionLayoutInflater(context);
            WEAK_HASH_MAP.put(context, layoutInflater);
        }
        return layoutInflater;
    }

    public static InjectionLayoutInflater from(Context context, LayoutInflater original) {
        InjectionLayoutInflater layoutInflater = WEAK_HASH_MAP.get(context);
        if (layoutInflater == null) {
            layoutInflater = new InjectionLayoutInflater(original, context);
            WEAK_HASH_MAP.put(context, layoutInflater);
        }
        return layoutInflater;
    }


    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new InjectionLayoutInflater(this, newContext);
    }

    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot, OnViewCreatedListener listener) {
        mOnViewCreatedListener = listener;
        View view = super.inflate(parser, root, attachToRoot);
        mOnViewCreatedListener = null;
        return view;
    }

    public View inflate(XmlPullParser parser, ViewGroup root, OnViewCreatedListener listener) {
        mOnViewCreatedListener = listener;
        View view = super.inflate(parser, root);
        mOnViewCreatedListener = null;
        return view;
    }

    public View inflate(int resource, ViewGroup root, OnViewCreatedListener listener) {
        mOnViewCreatedListener = listener;
        View view = super.inflate(resource, root);
        mOnViewCreatedListener = null;
        return view;
    }

    public View inflate(int resource, ViewGroup root, boolean attachToRoot, OnViewCreatedListener listener) {
        mOnViewCreatedListener = listener;
        View view = super.inflate(resource, root, attachToRoot);
        mOnViewCreatedListener = null;
        return view;
    }

/*
     @Override
    public View inflate(XmlPullParser parser, ViewGroup root, boolean attachToRoot) {
        return super.inflate(parser, root, attachToRoot);
    }

    @Override
    public View inflate(XmlPullParser parser, ViewGroup root) {
        return super.inflate(parser, root);
    }

    @Override
    public View inflate(int resource, ViewGroup root) {
        return super.inflate(resource, root);
    }

    @Override
    public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
        return super.inflate(resource, root, attachToRoot);
    }*/

    protected View onCreateView(View parent, String name, AttributeSet attrs, OnViewCreatedListener listener) throws ClassNotFoundException {
        mOnViewCreatedListener = listener;
        View view = this.onCreateView(parent, name, attrs);
        mOnViewCreatedListener = null;
        return view;
    }

    protected View onCreateView(String name, AttributeSet attrs, OnViewCreatedListener listener) throws ClassNotFoundException {
        mOnViewCreatedListener = listener;
        View view = this.onCreateView(name, attrs);
        mOnViewCreatedListener = null;
        return view;
    }

    @Override
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = super.onCreateView(parent, name, attrs);
        if (mOnViewCreatedListener != null) {
            return mOnViewCreatedListener.onViewCreated(getContext(), parent, view, attrs);
        }
        return view;
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        View view = null;

        if (-1 == name.indexOf('.')) {
            for (String prefix : sClassPrefixList) {
                try {
                    view = createView(name, prefix, attrs);
                    if (view != null) {
                        break;
                    }
                } catch (ClassNotFoundException e) {
                }
            }
        } else {
            view = createView(name, null, attrs);
        }

        if (view == null) {
            view = super.onCreateView(name, attrs);
        }
        if (mOnViewCreatedListener != null) {
            return mOnViewCreatedListener.onViewCreated(getContext(), null, view, attrs);
        }
        return view;
    }

    @Override
    public void setFactory2(Factory2 factory) {
        super.setFactory2(factory);
    }

    @Override
    public void setFactory(Factory factory) {
        super.setFactory(factory);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        try {
            return onCreateView(name, attrs);
        } catch (ClassNotFoundException e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name);
            ie.initCause(e);
            throw ie;
        }
    }

    public interface OnViewCreatedListener {
        View onViewCreated(Context context, View parent, View view, AttributeSet attrs);
    }

    public static OnViewCreatedListener getViewOnClickListenerInjector(final View.OnClickListener clickListener) {
        if (clickListener == null) {
            return null;
        }
        return new OnViewCreatedListener() {
            @Override
            public View onViewCreated(Context context, View parent, View view, AttributeSet attrs) {
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.View);
                if (a.getBoolean(R.styleable.View_injectListener, false)) {
                    view.setOnClickListener(clickListener);
                }
                a.recycle();
                return view;
            }
        };
    }

    public static OnViewCreatedListener getViewInjector(final Object object) {
        if (object == null) {
            return null;
        }
        return new OnViewCreatedListener() {
            @Override
            public View onViewCreated(Context context, View parent, View view, AttributeSet attrs) {
                ViewInjectProcessor.process(object, view);
                return view;
            }
        };
    }

    public static OnViewCreatedListener merge(final OnViewCreatedListener... listeners) {
        if (listeners == null) {
            return null;
        }
        return new OnViewCreatedListener() {
            @Override
            public View onViewCreated(Context context, View parent, View view, AttributeSet attrs) {
                for (OnViewCreatedListener listener : listeners) {
                    view = listener.onViewCreated(context, parent, view, attrs);
                }
                return view;
            }
        };
    }

}

