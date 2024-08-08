package com.developbharat.crunchhttp.common


sealed class Resource<T>(val data: T? = null, val status: ResourceStatus) {
    class ResourceSuccess<T>(data: T, status: String = "") :
        Resource<T>(data, ResourceStatus(isSuccess = true, statusText = status))

    class ResourceError<T>(status: String) :
        Resource<T>(null, ResourceStatus(isError = true, statusText = status))

    class ResourceInProgress<T>(status: String) :
        Resource<T>(null, ResourceStatus(isInProgress = true, statusText = status))
}