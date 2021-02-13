# Waround - What around
    
## App architecture   
  The app is written using the **MVVM** pattern with **Dagger2** for DI. 
  Basically the app is composed by a main component (*AppComponent*) which includes other main modules such us the *RetrofitModule*, and subcomponents such as the *MapsComponent* module, which again includes its own modules.

## App testing
There are few test settled up in order to test 
- the repo's responses returning the correct model and 
- a few main workflow test about the *MapsViewModel* 

## Work in progress
There are few TO-DOs in the source code, which suggest how to implement new features, improve current source code and so on.
