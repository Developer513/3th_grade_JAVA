package assignment6;
import java.text.ParseException;    //구문 분석 중 오류가 예기치 않게 도달했음을 나타내는 클래스 
//파싱은 어떤 페이지(문서, html 등)에서 내가 원하는 데이터를 특정 패턴이나 순서로 추출해 가공하는 것을 말한다.
//먼저 파서는 컴파일의 일부로서 원시 프로그램의 명령문이나 온라인 명령문, HTML 문서 등에서 MArkup Tag 등을
// 입력으로 받아들여서 구문을 해석할 수 있는 단위와 여러 부분으로 분할해주는 역할을 한다. 이러한 파서(parser) 
//역할을 하는 컴퓨터가 구문 트리(parse tree)로 재구성하는 구문 분석 과정을 뜻한다.
import java.text.SimpleDateFormat;//현재날짜를 구하기 위한 클래스 
import java.util.List;//동적배열 클래스 
import java.util.*;


public class Account {
   private double bal; // The current balance
   private int accnum; // The account number

   public Account(int a) {
      bal = 0.0;
      accnum = a;
   }

   public void deposit(double sum) {
      if (sum > 0)
         bal += sum;
      else
         System.err.println("Account.deposit(...): " + "cannot deposit negative amount.");
   }

   public void withdraw(double sum) {
      if (sum > 0)
         bal -= sum;
      else
         System.err.println("Account.withdraw(...): " + "cannot withdraw negative amount.");
   }

   public double getBalance() {
      return bal;
   }

   public int getAccountNumber() {
      return accnum;
   }

   public String toString() {
      return "Acc " + accnum + ": " + "balance = " + bal;
   }

   public final void print() {
      // Don't override this
      System.out.println(toString());
   }
}

class SavingAccount extends Account {
   private double interest_rate = 1.21; //이자율 

   public SavingAccount(int a) {        //슈퍼클래스 지정자 상속
      super(a);
   }

   public double getInterestrate() {    //이자율 리턴 
      return interest_rate;
   }

   public void giveInterest() {         //이자 지급 
      deposit(getBalance() * interest_rate/100);// 상속메소드를 통해 bal 간접 접근
      System.out.println("You earned "+getBalance() * interest_rate/100+"% interest");
   }
}

class StudentAccount extends SavingAccount {
   private String drawlimit;
   public StudentAccount(int a, String drawlimit) {//인출가능시점 설정 
      super(a);
      this.drawlimit = drawlimit;
   } 

   @Override
   public double getInterestrate() {// SavingAccount 클래스 메소드 오버라이딩 
      double studentrate = super.getInterestrate()*2;//슈퍼클래스의 이자율 두배 
      return studentrate;
   }

   @Override
   public void giveInterest() {         //이자 지급 
      deposit(getBalance() * getInterestrate()/100);// 상속메소드를 통해 bal 간접 접근
      System.out.println("You earned "+getBalance() * getInterestrate()/100+"% interest");
   }

   @Override
   public void withdraw(double sum) {// SavingAccount 클래스 메소드 오버라이딩
      try {//parse() 를 쓰려면 예외처리문이 필요함 
         SimpleDateFormat format = new SimpleDateFormat("yyyy");// 년도만 되로록 하는 객체 생성
         Date toDay = new Date();//날짜를 저장할 객체 생성 
         String presentYear = format.format(toDay);//오늘날짜를 yyyy(년도만) 형식으로 문자열 저장
         Date year = format.parse(presentYear);//parse(): 시간을 의미하는 문자열을 해석해서 밀리세컨드를 리턴
         Date end = format.parse(this.drawlimit);
         if(year.compareTo(end)==0){//인출시점 판별  즉 만기날짜와 오늘이 일치하면  compareTo 가 0  
     //반환기준값.compareTo( 비교대상 )
               super.withdraw(sum);
            }
         else System.out.println("It's not expiration date yet");     
      } 
      catch (ParseException e) {
         System.out.println("ERROR");
         e.printStackTrace();
      }
   }
   public void getdrawlimit(){
      System.out.println(drawlimit);
   }
}
class CheckingAccount extends Account{
   private double overdraftlim;
   public CheckingAccount(int a, double overdraftlim){//초과인출한도를 지정자에서 요구 
      super(a);
      this.overdraftlim = overdraftlim;
     // deposit(overdraftlim);                        //객체 생성하자마자 초과인출금 지급
   }
   @Override
   public void withdraw(double sum){
      if(sum <= getBalance()+overdraftlim){           //인출한도 초과 유무 판별 
         super.withdraw(sum);
      }
      else
      System.out.println("bankruptcy");
   }
}

class Bank {
   List<Account> accountList; // 동적배열 선언 
   private int index = 0;
   public Bank() {
      this.accountList = new LinkedList<Account>();
   }
   public void creatAccount(){//기능 메소드  문자열을 넣으면 그것과 맞는 객체 생성 및 배열 할당 
      Scanner sc = new Scanner(System.in);
      System.out.println("Please enter the number of type what you want");
      System.out.println("1.Regular Account  2.Saving Account  3.Student Account(withdraw limit)  4.CheckingAccount");
      int select = sc.nextInt();
      System.out.print("Set the account number: ");
      int acnum = sc.nextInt();
      if(select == 1){
         accountList.add(new Account(acnum));
         index++;
      }
      if(select == 2){
         System.out.println("You choose Saving Account you can earn 1.21% interest!");
         accountList.add(new SavingAccount(acnum));
         index++;
      }
      if(select == 3){
         System.out.println("You choose Student Account set the withdraw limit year");
         System.out.println("Example: insert \"2020\" ");
         String withdrawlimit = sc.next();
         accountList.add(new StudentAccount(acnum,withdrawlimit));
         index++;
         System.out.println("Account can't withdraw until "+withdrawlimit+", however you can earn 2.42% interest!");
      }
      if(select == 4){
         System.out.println("You choose Checking Account set the overdraft limit");
         System.out.println("Example: insert \"3\" ");
         double overdraftlimit = sc.nextDouble();
         accountList.add(new CheckingAccount(acnum,overdraftlimit));
         index++;
      }
   }
   public void deleteAccount(){//입력받은 accnum 과 일치하는 객체를 찾아 해당객체 삭제 
      Scanner sc = new Scanner(System.in);
      System.out.println("※CAUTION※ you must withdraw all blance before deletion");
      System.out.print("Please enter the account number to delete: ");
      Integer delaccountnum = sc.nextInt();//기본형이 아니라 참조형으로 바꾸면 call by value 방식으로 비교하는 .equals 를 사용할수 있다. 
      for(int i=0;i<accountList.size();i++){
         //Account a = accountList.get(i);
         if(accountList.get(i).getAccountNumber() == delaccountnum){ accountList.remove(i);
//         if(delaccountnum.equals(accountList[i].getAccountNumber()))  {
            System.out.println("Account deletion Successful!");
       }
      }
   }
   public void giveInterest(){// 생생된 계좌 중 이자를 지급해야하는 계좌에 대해 이자 지급
      for(int i=0;i<accountList.size();i++){
         //Account a = accountList.get(i);
         if(accountList.get(i) instanceof SavingAccount || accountList.get(i) instanceof StudentAccount){
            if(accountList.get(i) instanceof SavingAccount){
               System.out.println("Interest was paid.");
               ((SavingAccount) accountList.get(i)).giveInterest();//다운캐스팅
            }
            else if(accountList.get(i) instanceof StudentAccount){
               System.out.println("Interest was paid.");
               ((StudentAccount) accountList.get(i)).giveInterest();//다운캐스팅
            }
         }
      }
   }
   public static void main(String[] args){
      Bank account = new Bank();
      //이자율 1.21 % , 학생 우대 이자율 2.42 %
      //account.creatAccount();//Account accnum = 0
      //account.creatAccount();//SavingAccount accnum = 1 
      //account.creatAccount();//CheckingAccount accnum = 2 overdraft limit = 3
      account.creatAccount();//StudentAccount accnum = 3 withdraw until = 2021
      //account.accountList.get(0).deposit(100);//Account에 100 입금
      account.accountList.get(0).deposit(100);//SavingAccount 에 100 입금 
      //account.accountList.get(1).deposit(100);//CheckingAccount 에 100 입금 
      //account.accountList.get(3).deposit(100);//CStudentAccount 에 100 입금 
      for(int i=0; i<account.accountList.size();i++){
         account.accountList.get(i).print();
      }
      account.giveInterest();// 지급이 되어야 하는 계좌에 이자지급 
      for(int i=0; i<account.accountList.size();i++){
         account.accountList.get(i).print();
      }
      //for(int i=0; i<account.accountList.size();i++){
         account.accountList.get(0).withdraw(100);
      //account.accountList.get(1).withdraw(103);//한도초과 인출
     // }
      for(int i=0; i<account.accountList.size();i++){
         account.accountList.get(i).print();
      }
      //account.deleteAccount();
      //account.deleteAccount(); 
      //for(int i=0; i<account.accountList.size();i++){
      //   account.accountList.get(i).print();
     // }
   }
}
