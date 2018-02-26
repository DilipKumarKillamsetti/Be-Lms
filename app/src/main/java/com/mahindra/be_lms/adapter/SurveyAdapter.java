package com.mahindra.be_lms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mahindra.be_lms.R;
import com.mahindra.be_lms.interfaces.Callback;
import com.mahindra.be_lms.lib.L;
import com.mahindra.be_lms.model.Survey;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chaitali Chavan on 11/18/16.
 * Modified By Chaitali Chavan on 11/18/16,
 */
public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.SurveyViewHolder> {
    private static final int FOOTER_VIEW = 1;
    public List<Survey> surveyList;
    private Context contxt;
    private String mStringRating;
    private Callback callback;

    public SurveyAdapter(Context context, List<Survey> surveyList) {
        this.contxt = context;
        this.surveyList = surveyList;
    }

    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_quizmaster_btn_layout, parent, false);

            FooterViewHolder vh = new FooterViewHolder(v);
            return vh;
        }

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_survey_layout, parent, false);

        NormalViewHolder vh = new NormalViewHolder(v);

        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == surveyList.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(SurveyViewHolder vh, final int position) {
        try {
            if (vh instanceof NormalViewHolder) {
                int qestn_no = position + 1;
                L.l(contxt, "In Normal view");
                final NormalViewHolder holder = (NormalViewHolder) vh;
                if (surveyList.get(position).getSurvey_question_type().equalsIgnoreCase("textarea")) {
                    L.l(contxt, "Descriptive");
                    holder.llDescriptiveMainLayout.setVisibility(View.VISIBLE);
                    holder.llRadioMainLayout.setVisibility(View.GONE);
                    holder.llMCQMainLayout.setVisibility(View.GONE);
                    holder.llRatingBarMainLayout.setVisibility(View.GONE);
                    holder.tvSurveyDescriptiveQuestion.setText("" + qestn_no + ". " + surveyList.get(position).getSurvey_question());
                    holder.etSurveyDescriptive.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            surveyList.get(position).setUser_answer(s.toString());
                            Log.e("=+=+=",s.toString());
                        }
                    });
                }else if (surveyList.get(position).getSurvey_question_type().equalsIgnoreCase("textfield")) {
                    L.l(contxt, "Descriptive");
                    holder.llDescriptiveMainLayout.setVisibility(View.VISIBLE);
                    holder.llRadioMainLayout.setVisibility(View.GONE);
                    holder.llMCQMainLayout.setVisibility(View.GONE);
                    holder.llRatingBarMainLayout.setVisibility(View.GONE);
                    holder.tvSurveyDescriptiveQuestion.setText("" + qestn_no + ". " + surveyList.get(position).getSurvey_question());
                    holder.etSurveyDescriptive.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            surveyList.get(position).setUser_answer(s.toString());
                        }
                    });
                }
                else if (surveyList.get(position).getSurvey_question_type().equalsIgnoreCase("multichoiceRated")) {
                    L.l(contxt, "MCQ");
                    holder.llDescriptiveMainLayout.setVisibility(View.GONE);
                    holder.llMCQMainLayout.setVisibility(View.VISIBLE);
                    holder.llRadioMainLayout.setVisibility(View.GONE);
                    holder.llRatingBarMainLayout.setVisibility(View.GONE);
                    holder.tvSurveyMCQQuestion.setText("" + qestn_no + ". " + surveyList.get(position).getSurvey_question());
                    List<String> opStringList = surveyList.get(position).getOptionArrayList();
                    for (int i = 0; i < opStringList.size(); i++) {
                        if (i == 0) {
                            L.l(contxt, "Option 1: " + opStringList.get(i));
                            if (holder.rbOption1.getVisibility() == View.GONE) {
                                holder.rbOption1.setVisibility(View.VISIBLE);
                            }
                            holder.rbOption1.setText(opStringList.get(i));
                        } else if (i == 1) {
                            L.l(contxt, "Option 2: " + opStringList.get(i));
                            if (holder.rbOption2.getVisibility() == View.GONE) {
                                holder.rbOption2.setVisibility(View.VISIBLE);
                            }
                            holder.rbOption2.setText(opStringList.get(i));
                        } else if (i == 2) {
                            L.l(contxt, "Option 3: " + opStringList.get(i));
                            if (holder.rbOption3.getVisibility() == View.GONE) {
                                holder.rbOption3.setVisibility(View.VISIBLE);
                            }
                            holder.rbOption3.setText(opStringList.get(i));
                        } else if (i == 3) {
                            L.l(contxt, "Option 4: " + opStringList.get(i));
                            if (holder.rbOption4.getVisibility() == View.GONE) {
                                holder.rbOption4.setVisibility(View.VISIBLE);
                            }
                            holder.rbOption4.setText(opStringList.get(i));
                        } else if (i == 4) {
                            L.l(contxt, "Option 5: " + opStringList.get(i));
                            if (holder.rbOption5.getVisibility() == View.GONE) {
                                holder.rbOption5.setVisibility(View.VISIBLE);
                            }
                            holder.rbOption5.setText(opStringList.get(i));
                        }
                    }
                    final JSONArray jsonArray = new JSONArray();
                    holder.rbOption1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            L.l(contxt, "Check state: " + isChecked);
                            if (isChecked) {
                                holder.rbOption1.setChecked(isChecked);
                                try {
                                    surveyList.get(position).getMcq_answer().put(0, holder.rbOption1.getText().toString());
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.rbOption1.setChecked(false);
                                try {
                                    surveyList.get(position).getMcq_answer().put(0, "");
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    holder.rbOption2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            L.l(contxt, "Check state 2: " + isChecked);
                            if (isChecked) {
                                holder.rbOption2.setChecked(isChecked);
                                try {
                                    surveyList.get(position).getMcq_answer().put(1, holder.rbOption2.getText().toString());
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.rbOption2.setChecked(false);
                                try {
                                    surveyList.get(position).getMcq_answer().put(1, "");
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    holder.rbOption3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            L.l(contxt, "Check state 3: " + isChecked);
                            if (isChecked) {
                                holder.rbOption3.setChecked(isChecked);
                                try {
                                    surveyList.get(position).getMcq_answer().put(2, holder.rbOption3.getText().toString());
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.rbOption3.setChecked(false);
                                try {
                                    surveyList.get(position).getMcq_answer().put(2, "");
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    holder.rbOption4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            L.l(contxt, "Check state 4: " + isChecked);
                            if (isChecked) {
                                holder.rbOption4.setChecked(isChecked);
                                try {
                                    surveyList.get(position).getMcq_answer().put(3, holder.rbOption4.getText().toString());
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.rbOption4.setChecked(false);
                                try {
                                    surveyList.get(position).getMcq_answer().put(3, "");
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    holder.rbOption5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            L.l(contxt, "Check state 5: " + isChecked);
                            if (isChecked) {
                                holder.rbOption5.setChecked(isChecked);
                                try {
                                    surveyList.get(position).getMcq_answer().put(4, holder.rbOption5.getText().toString());
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                holder.rbOption5.setChecked(false);
                                try {
                                    surveyList.get(position).getMcq_answer().put(4, "");
                                    L.l(contxt, "MCQ JSON ARRAY: " + surveyList.get(position).getMcq_answer().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                   /* holder.rgSurveyMCQ.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId== R.id.rbSurveyOption1){
                                surveyList.get(position).setUser_answer(holder.rbOption1.getText().toString());
                            }else if (checkedId== R.id.rbSurveyOption2){
                                surveyList.get(position).setUser_answer(holder.rbOption2.getText().toString());
                            }else if (checkedId== R.id.rbSurveyOption3){
                                surveyList.get(position).setUser_answer(holder.rbOption3.getText().toString());
                            }else if (checkedId== R.id.rbSurveyOption4){
                                surveyList.get(position).setUser_answer(holder.rbOption4.getText().toString());
                            }else if (checkedId== R.id.rbSurveyOption5){
                                surveyList.get(position).setUser_answer(holder.rbOption5.getText().toString());
                            }
                        }
                    });*/

                } else if (surveyList.get(position).getSurvey_question_type().equalsIgnoreCase("Rating")) {
                    L.l(contxt, "Rating");
                    mStringRating = "0.0";
                    holder.llDescriptiveMainLayout.setVisibility(View.GONE);
                    holder.llMCQMainLayout.setVisibility(View.GONE);
                    holder.llRadioMainLayout.setVisibility(View.GONE);
                    holder.llRatingBarMainLayout.setVisibility(View.VISIBLE);
                    holder.tvSurveyRatingQuestion.setText("" + qestn_no + ". " + surveyList.get(position).getSurvey_question());
                    holder.ratingBarSurvey.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            mStringRating = String.valueOf(rating);
                            L.l(contxt, "Rating: " + mStringRating);
                            surveyList.get(position).setUser_answer(mStringRating);
                        }
                    });
                }else if(surveyList.get(position).getSurvey_question_type().equalsIgnoreCase("multichoice")){
                    holder.llDescriptiveMainLayout.setVisibility(View.GONE);
                    holder.llMCQMainLayout.setVisibility(View.GONE);
                    holder.llRadioMainLayout.setVisibility(View.VISIBLE);
                    holder.llRatingBarMainLayout.setVisibility(View.GONE);
                    holder.tvSurveyRadioQuestion.setText("" + qestn_no + ". " + surveyList.get(position).getSurvey_question());
                    holder.options.removeAllViews();
                    String chices = surveyList.get(position).getSurvey_question_options();
                    String[] options1 =chices.split("\\|");
                    for (int i = 0; i <= options1.length; i++) {
                        RadioButton rbn = new RadioButton(contxt);
                       rbn.setId(i+1);
                        rbn.setText(options1[i]);
                        //Attach button to RadioGroup.
                        holder.options.addView(rbn);
                    }



                }

            } else if (vh instanceof FooterViewHolder) {
                FooterViewHolder holder = (FooterViewHolder) vh;
                L.l(contxt, "In footer view");
                if (surveyList.size() > 0) {
                    holder.btnQuizListMasterSubmit.setVisibility(View.VISIBLE);
                } else {
                    holder.btnQuizListMasterSubmit.setVisibility(View.GONE);
                }
                holder.btnQuizListMasterSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.myCallback(0);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setmCallback(Callback mCallback) {
        this.callback = mCallback;
    }

    @Override
    public int getItemCount() {
        if (surveyList == null) {
            return 0;
        }
        if (surveyList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }
        // Add extra view to show the footer view
        return surveyList.size() + 1;
    }

    // Now define the viewholder for Normal list item
    public class NormalViewHolder extends SurveyViewHolder {
        @BindView(R.id.llDescriptiveMainLayout)
        LinearLayout llDescriptiveMainLayout;
        @BindView(R.id.llMCQMainLayout)
        LinearLayout llMCQMainLayout;
        @BindView(R.id.llRatingBarMainLayout)
        LinearLayout llRatingBarMainLayout;
        @BindView(R.id.tvSurveyDescriptiveQuestion)
        TextView tvSurveyDescriptiveQuestion;
        @BindView(R.id.etSurveyDescriptive)
        EditText etSurveyDescriptive;
        @BindView(R.id.tvSurveyMCQQuestion)
        TextView tvSurveyMCQQuestion;
        @BindView(R.id.rbSurveyOption1)
        CheckBox rbOption1;
        @BindView(R.id.rbSurveyOption2)
        CheckBox rbOption2;
        @BindView(R.id.rbSurveyOption3)
        CheckBox rbOption3;
        @BindView(R.id.rbSurveyOption4)
        CheckBox rbOption4;
        @BindView(R.id.rbSurveyOption5)
        CheckBox rbOption5;
        @BindView(R.id.tvSurveyRatingQuestion)
        TextView tvSurveyRatingQuestion;
        @BindView(R.id.ratingBarSurvey)
        RatingBar ratingBarSurvey;
        @BindView(R.id.options)
        RadioGroup options;
        @BindView(R.id.llRadioMainLayout)
        LinearLayout llRadioMainLayout;
        @BindView(R.id.tvSurveyRadioQuestion)
        TextView tvSurveyRadioQuestion;

        public NormalViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            // btn_removefromcart = (Button) itemView.findViewById(R.id.btn_removefrmCart);
          options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub

                    RadioButton btn = (RadioButton) options.findViewById(checkedId);
                    int idx = options.indexOfChild(btn);
                    int pos = idx+1;
                    surveyList.get(getPosition()).setUser_answer(""+pos);

                }
            });
        }
    }

    // Define a view holder for Footer view
    public class FooterViewHolder extends SurveyViewHolder {
        @BindView(R.id.btnQuizListMasterSubmit)
        Button btnQuizListMasterSubmit;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {
        public SurveyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
