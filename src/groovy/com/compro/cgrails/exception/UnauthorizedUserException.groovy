package com.compro.cgrails.exception

class UnauthorizedUserException extends RuntimeException {
	public UnauthorizedUserException(){
		super();
	}

	public UnauthorizedUserException(String message){
		super(message);
	}
}
