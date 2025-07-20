package com.example.prm392app.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.prm392app.model.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<Application>> applications = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NotificationsViewModel() {
        loadApplications();
    }

    public LiveData<List<Application>> getApplications() {
        return applications;
    }

    private void loadApplications() {
        String studentId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (studentId != null) {
            db.collection("applications")
                    .whereEqualTo("studentId", studentId)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            applications.setValue(new ArrayList<>());
                            return;
                        }
                        if (value != null) {
                            List<Application> appList = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                Application app = doc.toObject(Application.class);
                                app.setApplicationId(doc.getId());
                                appList.add(app);
                            }
                            applications.setValue(appList);
                        }
                    });
        }
    }
}