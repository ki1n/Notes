package com.example.notes.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.example.notes.App;
import com.example.notes.R;
import com.example.notes.activities.details.NewNotesActivity;
import com.example.notes.model.Note;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.NoteViewHolder> {
    // работает на уровне рефлексии
    private SortedList<Note> sortedList;

    public Adapter() {
        sortedList = new SortedList<>(Note.class, new SortedList.Callback<Note>() {
            // метод сравнивает меду собой 2 обекта
            @Override
            public int compare(Note o1, Note o2) {
                if (!o2.date.isEmpty() && o1.date.isEmpty()) {
                    return 1;
                }
                if (o2.date.isEmpty() && !o1.date.isEmpty()) {
                    return -1;
                }

                return (o1.date).compareTo(o2.date);
            }

            // вызвается если элемент в позиции меняется
            @Override
            public void onChanged(int position, int count) {
                // сообщяем адаптер что эл изменились в таком то диапазоне и обновит без затрагиваня ост-х эл-в
                notifyItemRangeChanged(position, count);
            }

            // возврашяется истина если 2 эл равны полностью
            @Override
            public boolean areContentsTheSame(Note oldItem, Note newItem) {
                // функция equals это делает
                return oldItem.equals(newItem);
            }

            // содержимое может быть разное но одинаковые айдишники
            // тот-же самый эл но поосле обновления
            @Override
            public boolean areItemsTheSame(Note item1, Note item2) {
                // вот и id пригодился
                return item1.uid == item2.uid;
            }

            // сообшения об изменении адаптеру
            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_note_list, parent, false));
    }

    // привязка конкретной заметки к вьюхолдеру
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bindNote(sortedList.get(position));
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    // функция котрая позволит обновить список сожержимое адаптера
    public void setItemsNote(List<Note> notes){
        sortedList.replaceAll(notes); // sortedList сравнит содержимое с тем что есть с новым и сам всё что нужно заменит
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView headingNote;
        TextView textNote;
        TextView dateNote;

        Note note;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);

            headingNote = itemView.findViewById(R.id.tv_item_heading);
            textNote = itemView.findViewById(R.id.tv_item_text);
            dateNote = itemView.findViewById(R.id.tv_item_data);

            // при нажатии редактируем
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewNotesActivity.start((Activity) itemView.getContext(), note);
                }
            });

            // при долгом нажатии удаляем заметку
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Activity) itemView.getContext());
                    builder.setTitle(R.string.warning);  // заголовок
                    builder.setMessage(R.string.del_text_note); // сообщение
                    builder.setIcon(R.drawable.ic_delete_note); // иконка

                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            App.getInstance().getNoteDao().delete(note);
                        }
                    });

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();  // покажем диалог

                    return true;
                }
            });
        }

        public void bindNote(Note note) {
            this.note = note;

            headingNote.setText(note.heading);
            textNote.setText(note.text);
            dateNote.setText(note.date);
        }

    }
}
