package com.example.my_virtual_shelf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_virtual_shelf.R;
import com.example.my_virtual_shelf.db.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookListFull; // Copia completa per il filtro
    private List<Book> books;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookLongClick(Book book);
        void onBookClick(Book book);
    }

    public BookAdapter(List<Book> books, OnBookClickListener listener) {
        this.books = books;
        this.bookListFull = new ArrayList<>(books);
        this.listener = listener;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        this.bookListFull = new ArrayList<>(books);
        notifyDataSetChanged();
    }

    // Filtra la lista in base alla query cercata
    public void filter(String query) {
        books.clear();
        if (query.isEmpty()) {
            books.addAll(bookListFull);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Book item : bookListFull) {
                if ((item.title != null && item.title.toLowerCase().contains(filterPattern)) ||
                        (item.author != null && item.author.toLowerCase().contains(filterPattern))) {
                    books.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.txtTitle.setText(book.title);
        holder.txtAuthor.setText(book.author);
        holder.txtIsbn.setText("ISBN: " + book.isbn);

        String statusText = (book.status != null && !book.status.isEmpty()) ? book.status : "Da leggere";
        holder.txtStatus.setText(statusText);

        Glide.with(holder.itemView.getContext())
                .load(book.coverUrl)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(holder.imgCover);

        // Click breve per modificare lo stato
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(book);
        });

        // Click prolungato per opzioni (Elimina)
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onBookLongClick(book);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtAuthor, txtIsbn, txtStatus;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
            txtIsbn = itemView.findViewById(R.id.txtIsbn);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}