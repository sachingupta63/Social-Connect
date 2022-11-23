package com.example.socialconnect.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialconnect.R;
import com.example.socialconnect.RelatedQuestionActivity;
import com.example.socialconnect.YourQuestionsActivity;


public class BottomSheetAskQuestionFragmentDialog extends BottomSheetDialogFragment {

    TextView relatedEt,yourQuestionsEt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bottom_sheet_ask_question_dialog, container, false);

        relatedEt=view.findViewById(R.id.tv_related_bsd_af);
        yourQuestionsEt=view.findViewById(R.id.tv_your_questions_bsd_af);

        relatedEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), RelatedQuestionActivity.class));
            }
        });
        yourQuestionsEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), YourQuestionsActivity.class));
            }
        });
        return view;

    }
}