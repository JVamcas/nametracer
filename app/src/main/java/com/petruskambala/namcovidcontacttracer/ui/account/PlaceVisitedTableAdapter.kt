package com.petruskambala.namcovidcontacttracer.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.petruskambala.namcovidcontacttracer.R
import com.petruskambala.namcovidcontacttracer.databinding.TableViewCellLayoutBinding
import com.petruskambala.namcovidcontacttracer.databinding.TableViewColumnHeaderLayoutBinding
import com.petruskambala.namcovidcontacttracer.databinding.TableViewRowHeaderLayoutBinding
import com.petruskambala.namcovidcontacttracer.model.Cell
import com.petruskambala.namcovidcontacttracer.model.ColumnHeader
import com.petruskambala.namcovidcontacttracer.model.RowHeader

class PlaceVisitedTableAdapter : AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {

    class CellViewHolder(
        var view: View,
        var data: Cell? = null
    ) : AbstractViewHolder(view)

    class ColumnHeaderViewHolder(
        var view: View,
        var data: ColumnHeader? = null
    ) : AbstractViewHolder(view)

    class RowHeaderViewHolder(
        var view: View,
        var data: RowHeader? = null
    ) : AbstractViewHolder(view)

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ColumnHeaderViewHolder {
        val binding = TableViewColumnHeaderLayoutBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),parent,false
        )
        println("Column header is ")

        return ColumnHeaderViewHolder(
            binding.root
        )
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        DataBindingUtil.getBinding<TableViewColumnHeaderLayoutBinding>(holder.itemView)?.apply {
            columnHeaderTextView.text = columnHeaderItemModel?.header?:"Unknown"
        }
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        DataBindingUtil.getBinding<TableViewRowHeaderLayoutBinding>(holder.itemView)?.apply {
            rowHeaderTextView.text = rowHeaderItemModel?.header?:"Unknown"
        }
    }

    override fun onCreateRowHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RowHeaderViewHolder {
        val binding = TableViewRowHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return RowHeaderViewHolder(
            (binding.root)
        )
    }

    override fun getCellItemViewType(position: Int): Int {

        return 0
    }

    override fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder {

        val binding = TableViewCellLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CellViewHolder(
            binding.root
        )
    }

    override fun onCreateCornerView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.table_view_corner_layout,parent,false)
    }

    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val binding = DataBindingUtil.getBinding<TableViewCellLayoutBinding>(holder.itemView)
        val data = cellItemModel?.value?:"Unknown"
        binding?.apply { cellData.text = data }
    }

    override fun getColumnHeaderItemViewType(position: Int): Int {
        return 0
    }

    override fun getRowHeaderItemViewType(position: Int): Int {
        return 0
    }
}