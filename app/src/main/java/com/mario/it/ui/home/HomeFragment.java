package com.mario.it.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mario.it.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView txtQuestionNumber, txtQuestion;
    private RadioGroup optionsGroup;
    private RadioButton option1, option2, option3, option4;
    private Button btnNext;

    private List<Question> questionsList = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        txtQuestionNumber = root.findViewById(R.id.txtQuestionNumber);
        txtQuestion = root.findViewById(R.id.txtQuestion);
        optionsGroup = root.findViewById(R.id.optionsGroup);
        option1 = root.findViewById(R.id.option1);
        option2 = root.findViewById(R.id.option2);
        option3 = root.findViewById(R.id.option3);
        option4 = root.findViewById(R.id.option4);
        btnNext = root.findViewById(R.id.btnNext);

        loadQuestions();

        btnNext.setOnClickListener(v -> {
            if (currentIndex < questionsList.size()) {
                checkAnswer();
                currentIndex++;
                if (currentIndex < questionsList.size()) {
                    showQuestion(currentIndex);
                } else {
                    Toast.makeText(getContext(), "Test Completed! Score: " + score + "/" + questionsList.size(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void loadQuestions() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://iti-it-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("questions");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionsList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Question q = ds.getValue(Question.class);
                    if (q != null) {
                        questionsList.add(q);
                    }
                }
                if (!questionsList.isEmpty()) {
                    showQuestion(0);
                } else {
                    Toast.makeText(getContext(), "No questions found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load questions: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showQuestion(int index) {
        Question q = questionsList.get(index);
        txtQuestionNumber.setText("Question " + (index + 1) + " of " + questionsList.size());
        txtQuestion.setText(q.getQuestion());
        option1.setText(q.getOption1());
        option2.setText(q.getOption2());
        option3.setText(q.getOption3());
        option4.setText(q.getOption4());
        optionsGroup.clearCheck();
    }

    private void checkAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Please select an option", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selected = getView().findViewById(selectedId);
        String answer = selected.getText().toString();

        if (answer.equals(questionsList.get(currentIndex).getAnswer())) {
            score++;
        }
    }
}