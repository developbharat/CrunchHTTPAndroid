package com.developbharat.crunchhttp.domain.uses.accounts

import com.developbharat.crunchhttp.domain.models.accounts.UserAccount
import com.developbharat.crunchhttp.domain.repos.accounts.IAccountsRepository
import javax.inject.Inject

class IsAuthenticatedUseCase @Inject constructor(
    private val accountsRepo: IAccountsRepository,
) {
    operator fun invoke(): UserAccount? {
        val account = accountsRepo.isAuthenticated()
        return account
    }
}