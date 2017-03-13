package io.github.mattlavallee.budgetbeaver.models.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.models.Tag;

public class TagCompletionView extends TokenCompleteTextView<Tag> {
    public TagCompletionView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Tag tag ){
        LayoutInflater layout = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) layout.inflate(R.layout.tag_token, (ViewGroup)getParent(), false);
        view.setText(tag.getTagName());

        return view;
    }

    @Override
    protected  Tag defaultObject(String completionText){
        return new Tag( -1, -1, completionText );
    }
}