package com.aptoide.amethyst.viewholders.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.viewholders.BaseViewHolder;
import com.aptoide.models.displayables.AppItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.EditorsChoiceRow;
import com.bumptech.glide.Glide;

/**
 * Created by rmateus on 02/06/15.
 */
public class EditorsChoiceViewHolder extends BaseViewHolder {

    public static final String EDITORS_CHOICE_STRING = "Editors Choice";
    public ImageView[] images;
    public TextView more;


    public EditorsChoiceViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
    }

    @Override
    public void populateView(Displayable displayable) {
//        EditorsChoiceViewHolder holder = (EditorsChoiceViewHolder) viewHolder;
        EditorsChoiceRow row = (EditorsChoiceRow) displayable;
        more.setOnClickListener(new BaseAdapter.IHasMoreOnClickListener(row, null));
        int i = 0;
        for (; i < images.length && i < row.appItemList.size(); i++) {
            final AppItem appItem = row.appItemList.get(i);
            Glide.with(itemView.getContext()).load(appItem.featuredGraphic).placeholder(R.drawable.placeholder_705x345).into(images[i]);
            appItem.category = EDITORS_CHOICE_STRING;
            images[i].setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem, i, false));
        }

        /*
        ImageView extraImage = (ImageView) itemView.findViewById(R.id.extra_image);
        if (extraImage != null && row.appItemList.size() > i) {
            AppItem appItem = row.appItemList.get(i);
            appItem.category = EDITORS_CHOICE_STRING;
            Glide.with(itemView.getContext()).load(appItem.featuredGraphic).placeholder(R.drawable.placeholder_705x345).into(extraImage);
            extraImage.setOnClickListener(new BaseAdapter.AppItemOnClickListener(appItem, i, false));
        }
        */

    }

    @Override
    protected void bindViews(View itemView) {
        //when it's a tablet, we have to fill the extra image
        boolean hasExtraImage = itemView.findViewById(R.id.extra_image) != null;
        images = new ImageView[hasExtraImage ? 4 : 3];
        images[0] = (ImageView)itemView.findViewById(R.id.main_image);
        images[1] = (ImageView)itemView.findViewById(R.id.left_image);
        images[2] = (ImageView) itemView.findViewById(R.id.right_image);
        if(hasExtraImage) images[3] = (ImageView) itemView.findViewById(R.id.extra_image);

        more = (TextView) itemView.findViewById(R.id.more);
    }
}
