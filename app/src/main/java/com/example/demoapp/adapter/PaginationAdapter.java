package com.example.demoapp.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.demoapp.R;
import com.example.demoapp.modal.Datum;
import com.example.demoapp.utils.PaginationAdapterCallback;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Datum> usersResults;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public PaginationAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        usersResults = new ArrayList<>();
    }

    public List<Datum> getUsersData() {
        return usersResults;
    }

    public void setUsersData(List<Datum> usersResults) {
        this.usersResults = usersResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_list, parent, false);
                viewHolder = new UserVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Datum result = usersResults.get(position);

        switch (getItemViewType(position)) {

            case ITEM:
                final UserVH userVH = (UserVH) holder;

                userVH.name.setText(result.getFirstName()+" "+result.getLastName());
                userVH.emailId.setText(result.getEmail());
                Glide.with(((UserVH) holder).avtar).load(result.getAvatar()).into(((UserVH) holder).avtar);
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return usersResults == null ? 0 : usersResults.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == usersResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;

    }

    public void add(Datum r) {
        usersResults.add(r);
        notifyItemInserted(usersResults.size() - 1);
    }

    public void addAll(List<Datum> usersResults) {
        for (Datum result : usersResults) {
            add(result);
        }
    }

    public void remove(Datum r) {
        int position = usersResults.indexOf(r);
        if (position > -1) {
            usersResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Datum());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = usersResults.size() - 1;
        Datum result = getItem(position);

        if (result != null) {
            usersResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Datum getItem(int position) {
        return usersResults.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(usersResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */


    /**
     * Main list's content ViewHolder
     */
    protected class UserVH extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView emailId;
        private CircleImageView avtar;

        public UserVH(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            emailId = itemView.findViewById(R.id.emailId);
            avtar = itemView.findViewById(R.id.avtar);
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

}
