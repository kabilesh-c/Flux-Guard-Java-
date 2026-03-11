# 🚀 Quick Start - Your Supabase is Ready!

## ✅ Supabase Configuration Complete

Your database credentials have been configured:
- **Host**: aws-1-ap-south-1.pooler.supabase.com
- **Database**: postgres
- **User**: postgres.soxsihcynqjkwbmchsqp
- **Password**: ******** (Check your Supabase dashboard)

## 📝 Next Steps

### Step 1: Rename Configuration File

```bash
cd d:\projects\Fraud-detection\backend
copy .env.supabase .env
```

Or manually:
1. Go to `backend` folder
2. Rename `.env.supabase` to `.env`

### Step 2: Install Frontend Dependencies

```bash
cd d:\projects\Fraud-detection\frontend
npm install
```

This will take 2-3 minutes.

### Step 3: Start Backend (Terminal 1)

```bash
cd d:\projects\Fraud-detection\backend
mvnw.cmd spring-boot:run
```

Wait for this message:
```
Started FraudDetectionApplication in X seconds
```

The backend will run on **http://localhost:8080**

### Step 4: Start Frontend (Terminal 2)

Open a NEW terminal and run:

```bash
cd d:\projects\Fraud-detection\frontend
npm run dev
```

The frontend will run on **http://localhost:5173**

### Step 5: Open Browser

Go to: **http://localhost:5173**

Login with:
- **Email**: `admin@fraud-detection.com`
- **Password**: `Admin@123`

---

## 🎯 What You'll See

1. **Login Page** - Dark theme with gradient
2. **Dashboard** - 4 KPI cards + 3 charts with sample data
3. **Transactions** - 8 pre-loaded transactions
4. **Rules** - 10 fraud detection rules
5. **Alerts** - 3 sample alerts

---

## 🔍 Verify Supabase Connection

Before starting, verify your Supabase has the data:

1. Go to Supabase Dashboard
2. Click **Table Editor**
3. You should see these tables:
   - users (3 rows)
   - transactions (8 rows)
   - rules (10 rows)
   - alerts (3 rows)
   - user_profiles (5 rows)

---

## 🐛 Troubleshooting

### Backend won't start?
- Check if port 8080 is free: `netstat -ano | findstr :8080`
- Verify `.env` file exists in `backend` folder
- Check Supabase connection in logs

### Frontend won't start?
- Make sure `npm install` completed successfully
- Check if port 5173 is free: `netstat -ano | findstr :5173`
- Verify `node_modules` folder exists

### Can't login?
- Make sure backend is running (check http://localhost:8080/actuator/health)
- Check browser console for errors (F12)
- Verify Supabase has the admin user

---

## 📊 Test the System

### 1. View Dashboard
- See KPI cards with transaction counts
- View charts with sample data

### 2. Create New Transaction
- Click "New Transaction" button
- Fill in details:
  - Transaction ID: TEST001
  - User ID: USR001
  - Amount: 15000
  - Currency: USD
- Submit and watch real-time evaluation!

### 3. View Transaction Detail
- Click on any transaction in the list
- See risk score breakdown
- View triggered rules

### 4. Manage Rules
- Go to Rules page
- Toggle rules on/off
- View rule expressions

---

## 🎨 Features to Explore

✅ **Real-time Alerts** - Submit high-value transaction and see instant toast notification  
✅ **Risk Scoring** - Watch transactions get evaluated in real-time  
✅ **Dashboard Analytics** - Interactive charts with Recharts  
✅ **WebSocket Status** - Green indicator when connected  
✅ **Responsive Design** - Try resizing the browser  
✅ **Dark Theme** - Beautiful glass-morphism effects  

---

## 📞 Need Help?

- **Backend Logs**: Check terminal where backend is running
- **Frontend Logs**: Open browser console (F12)
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

---

**Ready to start? Run the commands above!** 🚀
