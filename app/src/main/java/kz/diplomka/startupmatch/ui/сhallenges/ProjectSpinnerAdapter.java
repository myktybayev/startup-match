package kz.diplomka.startupmatch.ui.сhallenges;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;

/**
 * Жоба атауы + инициал белгісі (Figma SendChallenge «Жобаңыз» өрісі).
 */
public class ProjectSpinnerAdapter extends ArrayAdapter<ProjectEntity> {

    public ProjectSpinnerAdapter(@NonNull Context context, @NonNull List<ProjectEntity> projects) {
        super(context, 0, projects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return bindView(convertView, parent, position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return bindView(convertView, parent, position);
    }

    private View bindView(@Nullable View convertView, @NonNull ViewGroup parent, int position) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.item_project_spinner_row, parent, false);
        }
        ProjectEntity p = getItem(position);
        if (p == null) {
            return row;
        }
        TextView initials = row.findViewById(R.id.text_initials);
        TextView name = row.findViewById(R.id.text_project_name);
        initials.setText(initialsFromName(p.getName()));
        name.setText(p.getName());
        return row;
    }

    @NonNull
    public static String initialsFromName(@NonNull String name) {
        String t = name.trim();
        if (t.isEmpty()) {
            return "?";
        }
        String[] parts = t.split("\\s+");
        if (parts.length >= 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase(Locale.ROOT);
        }
        if (t.length() >= 2) {
            return t.substring(0, 2).toUpperCase(Locale.ROOT);
        }
        return t.toUpperCase(Locale.ROOT);
    }
}
