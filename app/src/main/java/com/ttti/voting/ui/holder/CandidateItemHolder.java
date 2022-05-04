package com.ttti.voting.ui.holder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.github.johnkil.print.PrintView;
import com.ttti.voting.R;
import com.ttti.voting.data.DataTree;

public class CandidateItemHolder extends TreeNode.BaseNodeViewHolder<CandidateItemHolder.IconTreeItem> {
    private TextView tvValue;
    private TextView tvTag;

    public CandidateItemHolder(Context context) {
        super(context);
    }
    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_candidate_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvTag = (TextView) view.findViewById(R.id.node_tag);

        tvValue.setText(value.dojo.getCategory());
        tvValue.setTypeface(tvValue.getTypeface(), Typeface.BOLD);
        tvTag.setText(value.dojo.getId());

        final PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));
        final CheckBox voteCheckBox = (CheckBox) view.findViewById(R.id.voteCheckBox);

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
    public void toggle(boolean active){
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
