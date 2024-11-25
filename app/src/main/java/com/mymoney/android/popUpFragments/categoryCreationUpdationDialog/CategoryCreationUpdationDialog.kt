package com.mymoney.android.popUpFragments.categoryCreationUpdationDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import com.mymoney.android.R
import com.mymoney.android.databinding.CategoryOperationDialogBinding
import com.mymoney.android.databinding.CheckboxItemBinding
import com.mymoney.android.popUpFragments.categoryCreationUpdationDialog.adapter.CategoryIconsAdapter
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.data.CategoryType
import com.mymoney.android.utils.CategoryIcon
import com.mymoney.android.utils.DialogListener
import com.mymoney.android.viewUtils.ViewUtils
import com.mymoney.android.viewUtils.ViewUtils.setRoundedRectangleBackgroundDrawable

class CategoryCreationUpdationDialog() : DialogFragment() {

    private lateinit var binding: CategoryOperationDialogBinding
    private var callback: DialogListener? = null
    private var dialogType: CategoryDialogType? = null
    private var selectedCategoryType: CategoryType = CategoryType.EXPENSE
    private var category: Category? = null
    private var selectedIcon: String = CategoryIcon.AWARDS.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding = CategoryOperationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRoundedRectangleBackgroundDrawable(view, Color.WHITE, 40f)
        if (category != null) {
            loadCategoryDate()
        }
        setupDialog()
        setUpOnClick()
        setUpRV()
    }

    private fun setUpRV() {
        val icons = CategoryIcon.entries
        val gridLayoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
        binding.iconsRecyclerView.layoutManager = gridLayoutManager
        val adapter = CategoryIconsAdapter(icons) { selectedIcon ->
            this.selectedIcon = selectedIcon.name
        }
        binding.iconsRecyclerView.adapter = adapter
        adapter.setSelectedIcon(selectedIcon)
    }

    private fun loadCategoryDate() {
        binding.nameEdt.setText(category?.name)
        this.selectedIcon = category?.icon.toString()
    }

    private fun setupDialog() {

        binding.nameEdt.requestFocus()

        if (dialogType?.name == CategoryDialogType.UPDATE.name) {
            binding.deleteBtn.visibility = View.VISIBLE
            binding.typeLL.visibility = View.GONE
            binding.textView2.text = getString(R.string.edit_category)
        } else {
            binding.deleteBtn.visibility = View.GONE
            binding.typeLL.visibility = View.VISIBLE
            binding.textView2.text = getString(R.string.add_new_category)
        }

        setUpCheckBox(binding.incomeCheckItem, CategoryType.INCOME)
        setUpCheckBox(binding.expenseCheckItem, CategoryType.EXPENSE)
    }

    private fun setUpCheckBox(
        checkboxItemBinding: CheckboxItemBinding,
        categoryType: CategoryType
    ) {
        checkboxItemBinding.titleTv.text = categoryType.name

        val isSelected = categoryType == selectedCategoryType
        handleChipStatus(checkboxItemBinding, isSelected)

        checkboxItemBinding.root.setOnClickListener {
            if (checkboxItemBinding.root.isSelected && isOnlyChipSelected()) {
                return@setOnClickListener
            }
            updateChipSelection(categoryType)
        }

    }

    private fun isOnlyChipSelected(): Boolean {
        return listOf(
            binding.incomeCheckItem.root.isSelected,
            binding.expenseCheckItem.root.isSelected,
        ).count { it } == 1
    }

    private fun updateChipSelection(selectedType: CategoryType) {
        handleChipStatus(binding.incomeCheckItem, selectedType == CategoryType.INCOME)
        handleChipStatus(binding.expenseCheckItem, selectedType == CategoryType.EXPENSE)
        selectedCategoryType = selectedType
    }

    private fun handleChipStatus(
        checkboxItemBinding: CheckboxItemBinding,
        isSelected: Boolean
    ) {
        checkboxItemBinding.root.isSelected = isSelected
        checkboxItemBinding.titleTv.setTextColor(
            if (!isSelected) Color.BLACK else ContextCompat.getColor(
                binding.root.context,
                R.color.green_500
            )
        )
        checkboxItemBinding.checkImg.visibility = if (isSelected) View.VISIBLE else View.GONE
    }


    override fun show(manager: FragmentManager, tag: String?) {
        val fragmentTransaction = manager.beginTransaction()
        fragmentTransaction.add(this, tag)
        fragmentTransaction.commitAllowingStateLoss()
    }

    private fun setUpOnClick() {
        binding.cancelBtn.setOnClickListener {
            callback?.onNegativeButtonClick()
            dismiss()
        }
        binding.deleteBtn.setOnClickListener {
            callback?.onSecondaryButtonClick()
            dismiss()
        }
        binding.saveBtn.setOnClickListener {
            saveNewCategory()
        }
    }

    private fun saveNewCategory() {

        if (binding.nameEdt.text.trim().isNullOrEmpty()) {
            ViewUtils.showToast(requireContext(), "Please fill required details")
            return
        }

        var newCategory: Category? = null
        if (category != null) {
            category?.let {
                newCategory = Category(
                    id = it.id,
                    name = binding.nameEdt.text.trim().toString(),
                    type = it.type,
                    icon = selectedIcon
                )

            }
        } else {
            newCategory = Category(
                name = binding.nameEdt.text.trim().toString(),
                type = selectedCategoryType.name,
                icon = selectedIcon
            )
        }

        newCategory?.let { callback?.onPositiveButtonClick(it) }
        dismiss()
    }


    companion object {
        private val TAG = "CategoryDialog"

        enum class CategoryDialogType {
            UPDATE, CREATE
        }

        fun showFragment(
            fragmentManager: FragmentManager,
            listener: DialogListener,
            dialogType: CategoryDialogType,
            category: Category? = null
        ) {
            val fragment = fragmentManager.findFragmentByTag(TAG) as? CategoryDialogType
            if (fragment != null) return
            CategoryCreationUpdationDialog().apply {
                this.callback = listener
                this.dialogType = dialogType
                this.category = category
            }.show(fragmentManager, TAG)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }
}