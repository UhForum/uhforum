package controllers;

import java.util.Map;
import models.Subject;
import models.UserInfo;
import models.UserInfoDB;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.formdata.EditProfileFormData;
import views.formdata.LoginFormData;
import views.formdata.SearchFormData;
import views.formdata.SignupFormData;
import views.formdata.SubjectTypes;
import views.formdata.TopicFormData;
import views.html.*;

/**
 * Handles user login, logout, signup and profiles.
 * @author eduardgamiao
 *
 */
public class Users extends Controller {
  
  
  public static Result postTopic() {
    Form<TopicFormData> formData = Form.form(TopicFormData.class);
    Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
    Map<Subject, Boolean> subjectTypeMap = SubjectTypes.getTypes();
    return ok(PostTopics.render("Post Topic", formData, searchFormData, subjectTypeMap));
  }
  /**
   * Returns the signup view.
   * @return The signup Result view.
   */
  public static Result signup() {
    Form<SignupFormData> formData = Form.form(SignupFormData.class);
    Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
    return ok(Signup.render("Signup", formData, searchFormData));
  }
  
  /**
   * Handles the post signup event.
   * @return The user's profile page.
   */
  public static Result postSignup() {
    Form<SignupFormData> formData = Form.form(SignupFormData.class).bindFromRequest();
    Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);    
    
    if (formData.hasErrors()) {
      return badRequest(Signup.render("Signup", formData, searchFormData));
    }
    SignupFormData data = formData.get();
    Long id = UserInfoDB.addUser(data);
    session().clear();
    session("email", data.email);
    return redirect(routes.Users.viewProfile(id));
  }
  
  /**
   * Renders the login page for the website.
   * @return The Login view.
   */
  public static Result login() {
    Form<LoginFormData> formData = Form.form(LoginFormData.class);
    Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
    return ok(Login.render("Login", formData, searchFormData));
  }
  
  /**
   * Handles post login functions.
   * @return The index page if successful, otherwise the login page.
   */
  public static Result postLogin() {
    Form<LoginFormData> formData = Form.form(LoginFormData.class).bindFromRequest();
    Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
    
    if (formData.hasErrors()) {
      return badRequest(Login.render("Login", formData, searchFormData));
    }
    else {
      session().clear();
      session("email", formData.get().email);
      return ok(index.render("UH Forum", searchFormData));
    }
  }
  
  /**
   * Returns the profile page.
   * @param id The ID of the user.
   * @return The user's profile page (if it exists). The index page if not a valid user.
   */
  public static Result viewProfile(Long id) {
    UserInfo user = UserInfoDB.getUser(id);
    Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
    if (user != null) {
      return ok(Profile.render("View Profile", user, searchFormData));
    }
    return redirect(routes.Application.index());
  }
  
  /**
   * User information management.
   * @param id The ID of the user.
   * @return The user edit page (if valid) or the index page.
   */
  @Security.Authenticated(Secured.class)
  public static Result editProfile(Long id) {
    UserInfo user = UserInfoDB.getUser(id);
    if (user != null) {
      EditProfileFormData data = new EditProfileFormData(user);
      Form<EditProfileFormData> formData = Form.form(EditProfileFormData.class).fill(data);
      Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
      return ok(EditProfile.render(formData, user, searchFormData));
    }
    return redirect(routes.Application.index());
  }
  
  @Security.Authenticated(Secured.class)
  public static Result postEditProfile(Long id) {
    Form<EditProfileFormData> formData = Form.form(EditProfileFormData.class).bindFromRequest();
    UserInfo user = UserInfoDB.getUser(id);
    if (formData.hasErrors() || user == null) {
      Form<SearchFormData> searchFormData = Form.form(SearchFormData.class);
      return badRequest(EditProfile.render(formData, user, searchFormData));
    }
    return redirect(routes.Users.viewProfile(id));
  }
  
  /**
   * Handles logout function.
   * @return The index page.
   */
  @Security.Authenticated(Secured.class)
  public static Result logout() {
    session().clear();
    return redirect(routes.Application.index());
  }
  
}
