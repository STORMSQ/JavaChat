package tw.com.stormsq.core;

import java.util.Vector;

import tw.com.stormsq.model.User;

public class UserList {

	private Vector<User> users = new Vector<User>();
	public UserList() 
	{
		
		users.add(new User(0,"�޲z��"));
		users.add(new User(1,"sten"));
		users.add(new User(2,"eden"));
		users.add(new User(3,"percy"));
		users.add(new User(4,"frank"));
	}
	public Vector<User> getList()
	{
		return this.users;
	}
}
