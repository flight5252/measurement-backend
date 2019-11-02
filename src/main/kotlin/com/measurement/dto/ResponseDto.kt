package com.measurement.dto

import com.measurement.controller.WrappedResponse

class ResponseDto<T>(
        code: Int? = null,
        data: T? = null,
        message: String? = null,
        status: ResponseStatus? = null

) : WrappedResponse<T>(code, data, message, status)