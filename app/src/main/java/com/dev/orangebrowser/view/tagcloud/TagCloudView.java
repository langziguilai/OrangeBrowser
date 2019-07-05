package com.dev.orangebrowser.view.tagcloud;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dev.orangebrowser.R;
import com.dev.util.DensityUtil;
import com.dev.util.Keep;
import com.dev.util.KeepMemberIfNecessary;
import com.dev.util.KeepNameIfNecessary;
import com.dev.view.recyclerview.GridDividerItemDecoration;
import com.noober.background.drawable.DrawableCreator;

import java.util.LinkedList;
import java.util.List;

@Keep
public class TagCloudView<T extends Tag> extends RecyclerView {
    private float tagTextSize;
    private int tagTextDefaultColor;
    private int tagTextDefaultBackgroundColor;
    private int tagTextSelectedColor;
    private int tagTextSelectedBackgroundColor;
    private float tagTextRadius;
    private int tagCloudBackgroundColor;
    private float tagCloudRadius;
    private List<T> tagList;
    private LinkedList<T> selectedTags;
    private T selectedTag;
    private boolean singleMode = true;
    private float tagTextBorderWidth;
    private int tagTextBorderColor;

    public TagCloudView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public TagCloudView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView(context);
    }

    public TagCloudView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        initView(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TagCloudView);
        if (ta != null) {
            tagTextSize = ta.getDimension(R.styleable.TagCloudView_tagTextSize, DensityUtil.dip2px(context, 14));
            tagTextDefaultColor = ta.getColor(R.styleable.TagCloudView_tagTextDefaultColor, 0x333333);
            tagTextDefaultBackgroundColor = ta.getColor(R.styleable.TagCloudView_tagTextDefaultBackgroundColor, 0x00ffffff);
            tagTextSelectedColor = ta.getColor(R.styleable.TagCloudView_tagTextSelectedColor, 0xEFEFEF);
            tagTextSelectedBackgroundColor = ta.getColor(R.styleable.TagCloudView_tagTextSelectedBackgroundColor, 0x999999);
            tagTextRadius = ta.getDimension(R.styleable.TagCloudView_tagTextRadius, DensityUtil.dip2px(context, 4));
            tagCloudBackgroundColor = ta.getColor(R.styleable.TagCloudView_tagCloudBackgroundColor, 0x00ffffff);
            tagCloudRadius = ta.getInt(R.styleable.TagCloudView_tagCloudRadius, 0);
            singleMode = ta.getBoolean(R.styleable.TagCloudView_singleChoice, true);
            tagTextBorderWidth = ta.getDimension(R.styleable.TagCloudView_tagTextBorderWidth, 0);
            tagTextBorderColor = ta.getColor(R.styleable.TagCloudView_tagTextBorderColor, 0x00ffffff);
            ta.recycle();
        }
    }

    private void initView(Context context) {
        Drawable tabCloudBackground = new DrawableCreator.Builder().setCornersRadius(tagCloudRadius)
                .setSolidColor(tagCloudBackgroundColor)
                .build();
        this.setBackground(tabCloudBackground);
        if (!singleMode) {
            selectedTags = new LinkedList<>();
        }
        setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        addItemDecoration(new GridDividerItemDecoration(DensityUtil.dip2px(context, 10), DensityUtil.dip2px(context, 10), 0x00fffff));
    }

    public void initData(List<T> tags) {
        this.tagList = tags;
        Adapter<TagViewHolder> tagAdapter = new RecyclerView.Adapter<TagViewHolder>() {
            @NonNull
            @Override
            public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TagViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
                T currentTag = tagList.get(position);
                holder.tv.setText(currentTag.getTag());
                holder.tv.setTextSize(tagTextSize);
                if (currentTag.getSelected()) {
                    holder.tv.setTextColor(tagTextDefaultColor);
                    Drawable defaultBackgroundDrawable = new DrawableCreator.Builder().setCornersRadius(tagTextRadius)
                            .setSolidColor(tagTextDefaultBackgroundColor)
                            .setStrokeColor(tagTextBorderColor)
                            .setStrokeWidth(tagTextBorderWidth)
                            .build();
                    holder.tv.setBackground(defaultBackgroundDrawable);
                } else {
                    holder.tv.setTextColor(tagTextSelectedColor);
                    Drawable selectedBackgroundDrawable = new DrawableCreator.Builder().setCornersRadius(tagTextRadius)
                            .setSolidColor(tagTextSelectedBackgroundColor)
                            .setStrokeColor(tagTextBorderColor)
                            .setStrokeWidth(tagTextBorderWidth)
                            .build();
                    holder.tv.setBackground(selectedBackgroundDrawable);
                }
                holder.tv.setOnClickListener(v -> {
                    if (singleMode) {
                        if (selectedTag != null) {
                            selectedTag.setSelected(false);
                        }
                        selectedTag = currentTag;
                        selectedTag.setSelected(true);
                    } else {
                        if (selectedTags.contains(currentTag)) {
                            currentTag.setSelected(false);
                        } else {
                            currentTag.setSelected(true);
                            selectedTags.add(currentTag);
                        }
                    }
                    notifyDataSetChanged();
                });
            }

            @Override
            public int getItemCount() {
                return tagList.size();
            }
        };
        setAdapter(tagAdapter);
    }

    public T getSelectedTag() {
        return selectedTag;
    }

    public List<T> getSelectedTags() {
        return selectedTags;
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tag);
        }
    }
}
