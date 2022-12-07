package com.market.supermarket.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleInvalidArgument(e: MethodArgumentNotValidException) : Map<String, String> {
        val errorMap = mutableMapOf<String,String>()
        e.bindingResult.fieldErrors.forEach { errorMap[it.field] = it.defaultMessage?:"" }

        return errorMap
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ShopProductBusinessException::class)
    fun handleBusinessException(e: ShopProductBusinessException) : Map<String, String> {
        val errorMap = mutableMapOf<String,String>()
        errorMap["errorMessage"] = e.message?:""

        return errorMap
    }

}