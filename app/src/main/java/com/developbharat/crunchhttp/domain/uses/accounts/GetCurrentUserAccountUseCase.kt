package com.developbharat.crunchhttp.domain.uses.accounts

import com.developbharat.crunchhttp.common.Resource
import com.developbharat.crunchhttp.domain.models.accounts.UserAccount
import com.developbharat.crunchhttp.domain.repos.accounts.IAccountsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetCurrentUserAccountUseCase @Inject constructor(
    private val accountsRepo: IAccountsRepository,
) {
    operator fun invoke(): Flow<Resource<UserAccount>> = flow {
        try {
            emit(Resource.ResourceInProgress("Checking: Am I logged in?"))
            val account = accountsRepo.whoami()
            emit(Resource.ResourceSuccess(account, "User is authenticated."))
        } catch (ex: IOException) {
            emit(Resource.ResourceError(ex.localizedMessage ?: "Unable to establish connection with server."))
        }  catch (ex: Exception) {
            val errorText = "Unknown error occurred. Please retry or contact us to help us fix it."
            emit(Resource.ResourceError(ex.localizedMessage ?: errorText))
        }
    }
}