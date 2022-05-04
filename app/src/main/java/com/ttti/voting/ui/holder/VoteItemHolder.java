package com.ttti.voting.ui.holder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import com.ttti.voting.R;
import com.github.johnkil.print.PrintView;
import com.ttti.voting.data.DataTree;

public class VoteItemHolder extends TreeNode.BaseNodeViewHolder<VoteItemHolder.IconTreeItem> {
    private TextView tvValue,tvTag;
    private PrintView arrowView;
    private CardView imageHolderView;

    public VoteItemHolder(Context context) {
        super(context);
    }
    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_vote_node, null, false);
        imageHolderView = (CardView) view.findViewById(R.id.imageholder);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvTag = (TextView) view.findViewById(R.id.node_tag);


        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));
        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        final CheckBox voteCheckBox = (CheckBox) view.findViewById(R.id.voteCheckBox);

        if(node.getLevel() > 1){
            arrowView.setVisibility(View.GONE);
            tvValue.setText(value.dojo.getFname() + " " + value.dojo.getLname());
            tvTag.setText(value.dojo.getId());
        }
        if(node.getLevel() == 1){
            tvTag.setText(value.dojo.getId());
            tvValue.setText(value.dojo.getCategory());
            tvValue.setTypeface(tvValue.getTypeface(), Typeface.BOLD);
            voteCheckBox.setVisibility(View.GONE);
            imageHolderView.setVisibility(View.GONE);
        }
        //node operations here
        voteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TreeNode parentnode = node.getParent();
                int count = parentnode.getChildren().size();
                for (int i = 0; i < count ; i++){
                    TreeNode childtreenode = parentnode.getChildren().get(i);
                    TreeNode.BaseNodeViewHolder tView =  childtreenode.getViewHolder();
                    View nodeview  = tView.getView();
                    CheckBox nodecheckbox = (CheckBox)nodeview.findViewById(R.id.voteCheckBox);
                    nodecheckbox.setChecked(false);
                }
                if(isChecked){
                    voteCheckBox.setChecked(true);
                }

            }
        });
        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right)); //temporary disabled
    }

    public static class IconTreeItem {
        public int icon;
        public DataTree dojo;

        public IconTreeItem(int icon, DataTree dojo) {
            this.icon = icon;
            this.dojo = dojo;
        }
    }
}
