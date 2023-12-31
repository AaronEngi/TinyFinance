package finance

import jgnash.engine.Account
import jgnash.engine.AccountType
import jgnash.engine.Transaction
import java.util.SortedSet

class AccountPerformance(val account: Account) {
    fun computeNetAssetValue(): Double {
        val share = computeShareTotalPair()
        return if (share.second == 0.0) {
            1.0
        } else {
            share.first / share.second
        }
    }

    private fun computeShareTotalPair(): Pair<Double, Double> {
        if (account.accountType in listOf(AccountType.SIMPLEINVEST)) {
            val treeTransactions: SortedSet<Transaction> = treeTransactions(account)

            //逐个计算净值
            var totalValue = 0.0
            var shareTotal = 0.0
            var shareValue = 1.0

            treeTransactions.forEach { tran ->
                val oldShareValue = shareValue

                //内部交易可能影响余额，不影响份额
                //调整交易
                val entries = tran.transactionEntries
                //single/double entry
                if (entries.size == 1) {
                    totalValue += tran.getAmount(account).toDouble()

                    //assert 账户size <=2
                    if (!tran.accounts.all { tranAccount -> tranAccount.ancestors.contains(account) } || shareTotal == 0.0) {
                        //1 涉外交易，修改份额
                        //2 初始化份额
                        shareTotal += tran.getAmount(account).toDouble() / oldShareValue
                    }
                } else {
                    //plan
                }

                if (shareTotal != 0.0) {
                    shareValue = totalValue / shareTotal
                }
            }
            return totalValue to shareTotal
        } else {
            return 1.0 to 1.0
        }
    }

    private fun treeTransactions(account: Account): SortedSet<Transaction> {
        val result: SortedSet<Transaction> = sortedSetOf()
        result.addAll(account.sortedTransactionList)
        account.children.forEach { result.addAll(treeTransactions(it)) }
        return result.sorted().toSortedSet()
    }
}