/*
 * Copyright (C) 2010 Marco Ratto
 *
 * This file is part of the project MqBridge.
 *
 * MqBridge is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * MqBridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MqBridge; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.co.marcoratto.util;

@SuppressWarnings("serial")
public class UtilityException extends Exception {

	public UtilityException() {
		super();
	}
	
	public UtilityException(String msg) {
		super(msg);
	}
	
	public UtilityException(Exception e) {
		super(e);
	}

	public UtilityException(Throwable t) {
		super(t);
	}
}
