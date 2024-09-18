package com.example.zoldseges.DAOS;

public interface ProductSelectorSellerView {
    // Called when an item is viewed
    void onItemView(int position);

    // Called when an item is modified
    void onItemModify(int position);

    // Called when an item is deleted
    void onItemDelete(int position);
}
