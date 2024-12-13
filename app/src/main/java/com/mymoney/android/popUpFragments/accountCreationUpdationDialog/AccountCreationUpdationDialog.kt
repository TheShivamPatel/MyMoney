package com.mymoney.android.popUpFragments.accountCreationUpdationDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymoney.android.R
import com.mymoney.android.databinding.AccountOperationDialogBinding
import com.mymoney.android.popUpFragments.accountCreationUpdationDialog.adapter.AccountIconsAdapter
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.utils.AccountIcon
import com.mymoney.android.utils.DialogListener
import com.mymoney.android.viewUtils.ViewUtils
import com.mymoney.android.viewUtils.ViewUtils.setRoundedRectangleBackgroundDrawable

class AccountCreationUpdationDialog() : DialogFragment() {

    private lateinit var binding: AccountOperationDialogBinding
    private var callback: DialogListener? = null
    private var dialogType: AccountDialogType? = null
    private var account: Account? = null
    private var selectedIcon: String = AccountIcon.CASH.name
    private lateinit var adapter: AccountIconsAdapter

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
        binding = AccountOperationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRoundedRectangleBackgroundDrawable(view, Color.WHITE, 40f)
        if (account != null) {
            loadAccountData()
        }
        setupDialog()
        setUpOnClick()
        setUpRV()
    }

    private fun setUpRV() {
        val icons = AccountIcon.entries
        binding.iconsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = AccountIconsAdapter(icons) { selectedIcon ->
            this.selectedIcon = selectedIcon.name
        }
        binding.iconsRecyclerView.adapter = adapter
        adapter.setSelectedIcon(selectedIcon)
    }

    private fun loadAccountData() {
        binding.nameEdt.setText(account?.name)
        binding.initialAmountEdt.setText(account?.balance.toString())
        this.selectedIcon = account?.icon!!
    }

    private fun setupDialog() {

        binding.nameEdt.requestFocus()

        if (dialogType?.name == AccountDialogType.UPDATE.name) {
            binding.deleteBtn.visibility = View.VISIBLE
            binding.inputOpeningAmountLL.visibility = View.GONE
            binding.textView2.text = getString(R.string.edit_account)
        } else {
            binding.deleteBtn.visibility = View.GONE
            binding.inputOpeningAmountLL.visibility = View.VISIBLE
            binding.textView2.text = getString(R.string.add_new_account)
        }
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
            saveNewAccount()
        }
    }

    private fun saveNewAccount() {

        if (binding.nameEdt.text.trim().isNullOrEmpty()) {
            ViewUtils.showToast(requireContext(), "Please fill required details")
            return
        }

        var newAccount: Account? = null
        if (account != null) {
            account?.let {
                newAccount = Account(
                    id = it.id,
                    name = binding.nameEdt.text.trim().toString(),
                    balance = binding.initialAmountEdt.text.toString().toDouble(),
                    icon = selectedIcon,
                )

            }
        } else {
            newAccount = Account(
                name = binding.nameEdt.text.trim().toString(),
                balance = binding.initialAmountEdt.text.toString().toDouble(),
                icon = selectedIcon,
            )
        }

        newAccount?.let { callback?.onAccountPositiveButtonClick(it) }
        dismiss()
    }

    companion object {
        private val TAG = "AccountDialog"

        enum class AccountDialogType {
            UPDATE, CREATE
        }

        fun showFragment(
            fragmentManager: FragmentManager,
            listener: DialogListener,
            dialogType: AccountDialogType,
            account: Account? = null
        ) {
            val fragment = fragmentManager.findFragmentByTag(TAG) as? AccountDialogType
            if (fragment != null) return
            AccountCreationUpdationDialog().apply {
                this.callback = listener
                this.dialogType = dialogType
                this.account = account
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