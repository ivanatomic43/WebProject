package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import beans.Reservation;
import beans.User;
import dto.ReservationDTO;
import enums.Role;
import enums.UserStatus;

public class UserDAO {

	
private HashMap<Integer, User> users=new HashMap<>();

	public HashMap<Integer, User> getUsers() {
		return users;
	}

	public void setUsers(HashMap<Integer, User> users) {
		this.users = users;
	}

	public UserDAO() {
		
	}
	
	public  UserDAO(String contextPath) {

		loadUsers(contextPath);
	}
	
	public void loadUsers(String contextPath) {
	System.out.println("ucitavanjee: "+contextPath);

		try {
			File file=new File(contextPath+ "/jsonFiles/users.json");
			ObjectMapper objectMapper=new ObjectMapper();
			objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

			
			User[] usersA=objectMapper.readValue(file, User[].class);
			
			for(User u:usersA) {
				users.put(u.getId(), u);
			}
			System.out.println(usersA.length+"a=velicina");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			
		}
	}
	
	public void addUser(User user,String contextPath) {
		int id=generateNewId();
		user.setId(id);
		user.setRole(Role.GUEST);
		user.setStatus(UserStatus.ACTIVE);
		users.put(id, user);
		saveUsers(contextPath);
	}
	
	public void addHost(User user,String contextPath) {
		int id=generateNewId();
		user.setId(id);
		user.setRole(Role.HOST);
		user.setStatus(UserStatus.ACTIVE);
		users.put(id, user);
		saveUsers(contextPath);
	}
	
	public int generateNewId() {
		int id=1;
		for(User u:users.values()) {
			if(u.getId()==id) {
				id++;
			}
		}
		return id;
	}
	
	public void saveUsers(String contextPath) {
		
		try {
			File file=new File(contextPath+"/jsonFiles/users.json");
			ObjectMapper objectMapper=new ObjectMapper();
			objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
			objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
			objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

			ArrayList<User> usersArray=new ArrayList<>();
			
			for(User u: users.values()) {
				usersArray.add(u);
			}
			
			System.out.println("aaaaa"+usersArray.size());
			objectMapper.writeValue(new File(contextPath + "/jsonFiles/users.json"),usersArray);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public boolean checkUserName(String username) {
		
		for(User u:users.values()) {
			if(u.getUsername().equals(username)) {
				return true;
			}
		}
		
		return false;

	}
	
	public User checkLogin(String username,String password) {
		
		for(User u:users.values()) {
			if(u.getUsername().equals(username)) {
				if(u.getPassword().equals(password)) {
					return u;
				}
			}
		}
		
		return null;
		
	}
	
	public void addApartmentForRent(String contextPath, int appId, int userId) {
		
		User user = users.get(userId);
		user.getApartmentsForRent().add(appId);
		users.replace(user.getId(), user);
		
		saveUsers(contextPath);
		
		
	}


	public ArrayList<User> getAllUsers(){
		ArrayList<User> ret = new ArrayList<>();
		for(User u: users.values()) {
			ret.add(u);
		}
		return ret;
	}
	
	public User getUserById(int id) {
		
	     
		
	     for(User u : users.values()) {
	    	 if(u.getId() == id){
	    		return u;
	    	 }
	     }
	     return null;
	}
	
	public void changeUserRole(int id) {
		
		
		for(User u: users.values()) {
			if(u.getId() == id) {
				if(u.getRole().equals(Role.GUEST)) {
					u.setRole(Role.HOST);
				} else if(u.getRole().equals(Role.HOST)) {
					u.setRole(Role.GUEST);
				}
			}
		}
		
		
	}
	
	
	public void changeUserStatus(int id) {
		
		
		for(User u: users.values()) {
			if(u.getId() == id) {
				if(u.getStatus().equals(UserStatus.ACTIVE)) {
					u.setStatus(UserStatus.BLOCKED);;
				} else if(u.getStatus().equals(UserStatus.BLOCKED)) {
					u.setStatus(UserStatus.ACTIVE);
				}
			}
		}
		
		
	}
	
	public Collection<User> searchUsersAdmin(User user){
		 ArrayList<User> usersList = new ArrayList<>();
		
		
		 String username = user.getUsername().toLowerCase();
		 String gender = user.getGender().toLowerCase();
		 String role = "";
		 
		 
		 if(user.getRole().equals("")) {
			 role = "";
		 } else {
			  role = user.getRole().toString().toLowerCase();
		 }
		 
		 
		 for(User u: users.values()) {
			 if(u.getUsername().toLowerCase().equals(username) && username != null && !usersList.contains(u)) {
				 usersList.add(u);
			 }
		 }
		 for(User u: users.values()) {
			 if(u.getRole().toString().toLowerCase().equals(role) && !usersList.contains(u) && role != null) {
				 usersList.add(u);
			 }
		 }
		 for(User u: users.values()) {
			 if(u.getGender().toLowerCase().equals(gender) && !usersList.contains(u) && gender != null) {
				 usersList.add(u);
			 }
		 }
		 
		 
		 
		 
		 Collections.reverse(usersList);
		 return usersList;
	}
	
	public Collection<User> searchUsersHost(User user, Collection<ReservationDTO> reservationList){
		 ArrayList<User> usersList = new ArrayList<>();
		 ArrayList<User> hostUsers = new ArrayList<>();
		 ArrayList<Integer> guests = new ArrayList<>();
		 
		 for(ReservationDTO r : reservationList) {
			 
			 	guests.add(r.getGuestID());
			 	System.out.println(r.getGuestID());
			 
		 }
		
		 for(int id : guests) {
			 
			 for(User u : users.values()) {
				 	
				 if(id == u.getId()) {
					 hostUsers.add(u);
				 }
				 
			 }
			 
		 }
		 String username = user.getUsername().toLowerCase();
		 String gender = user.getGender().toLowerCase();
		 String role = "";
		 
		 
		 if(user.getRole().equals("")) {
			 role = "";
		 } else {
			  role = user.getRole().toString().toLowerCase();
		 }
		 
		 
		 
		 for(User u: hostUsers) {
			 if(u.getUsername().toLowerCase().equals(username) && username != null && !usersList.contains(u)) {
				 usersList.add(u);
			 }
		 }
		 for(User u: hostUsers) {
			 if(u.getRole().toString().toLowerCase().equals(role) && !usersList.contains(u) && role != null) {
				 usersList.add(u);
			 }
		 }
		 for(User u: hostUsers) {
			 if(u.getGender().toLowerCase().equals(gender) && !usersList.contains(u) && gender != null) {
				 usersList.add(u);
			 }
		 }
		 
		 
		 
		 
		 Collections.reverse(usersList);
		 return usersList;
	}
	
	public void addReservation(String contextPath, int appId, int userId) {
		
		User user = users.get(userId);
		user.getReservationList();
		users.replace(user.getId(), user);
		
		saveUsers(contextPath);
		
		
	}
	
	public ArrayList<User> getAllUsersHost(Collection<ReservationDTO> reservationList){
		
		ArrayList<User> usersList = new ArrayList<>();
		ArrayList<Integer> usersID = new ArrayList<>();		
		
		for(ReservationDTO r : reservationList) {
			
			usersID.add(r.getGuestID());
			
		}
		
		
		for(User u : users.values()) {
			
			for( int id : usersID) {
				 if(u.getId() == id) {
					 usersList.add(u);
				 }
				
			}
		}
		
		
		return usersList;
		
		
		
	}
	
}

	


