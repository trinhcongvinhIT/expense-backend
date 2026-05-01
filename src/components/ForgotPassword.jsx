import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const ForgotPassword = () => {
    const [step, setStep] = useState(1); // 1: Nhập Email | 2: Nhập OTP | 3: Mật khẩu mới
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [message, setMessage] = useState({ text: '', type: '' });
    const [loading, setLoading] = useState(false); // Thêm cái này để khóa nút khi đang tải

    const navigate = useNavigate();

    // 1. Gửi yêu cầu lấy mã OTP
    const handleSendOtp = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await axios.post('http://localhost:8080/api/auth/forgot-password', { email });
            setMessage({ text: res.data, type: 'success' });
            setStep(2);
        } catch (error) {
            setMessage({ text: error.response?.data || 'Lỗi gửi email!', type: 'error' });
        } finally {
            setLoading(false);
        }
    };

    // 2. Xác nhận mã OTP
    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await axios.post('http://localhost:8080/api/auth/verify-otp', { email, otp });
            setMessage({ text: res.data, type: 'success' });
            setStep(3);
        } catch (error) {
            setMessage({ text: error.response?.data || 'Mã OTP không hợp lệ!', type: 'error' });
        } finally {
            setLoading(false);
        }
    };

    // 3. Đổi mật khẩu mới
    const handleResetPassword = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const res = await axios.post('http://localhost:8080/api/auth/reset-password', { email, newPassword });
            setMessage({ text: res.data, type: 'success' });
            setTimeout(() => navigate('/login'), 2000);
        } catch (error) {
            setMessage({ text: error.response?.data || 'Lỗi đổi mật khẩu!', type: 'error' });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md">
                <h2 className="text-2xl font-bold text-center mb-6">Quên Mật Khẩu</h2>

                {message.text && (
                    <div className={`p-3 mb-4 rounded ${message.type === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                        {message.text}
                    </div>
                )}

                {/* Bước 1: Nhập Email */}
                {step === 1 && (
                    <form onSubmit={handleSendOtp}>
                        <div className="mb-4">
                            <label className="block text-gray-700 mb-2">Nhập Email đăng ký:</label>
                            <input type="email" required className="w-full px-3 py-2 border rounded-md"
                                   value={email} onChange={(e) => setEmail(e.target.value)} />
                        </div>
                        <button type="submit" disabled={loading} className={`w-full text-white py-2 rounded-md transition ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-500 hover:bg-blue-600'}`}>
                            {loading ? 'Đang gửi...' : 'Gửi mã xác nhận'}
                        </button>
                    </form>
                )}

                {/* Bước 2: Nhập OTP */}
                {step === 2 && (
                    <form onSubmit={handleVerifyOtp}>
                        <div className="mb-4">
                            <label className="block text-gray-700 mb-2">Mã OTP (6 số):</label>
                            <input type="text" required className="w-full px-3 py-2 border rounded-md tracking-widest text-center text-lg"
                                   value={otp} onChange={(e) => setOtp(e.target.value)} maxLength="6" />
                        </div>
                        <button type="submit" disabled={loading} className={`w-full text-white py-2 rounded-md transition ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-green-500 hover:bg-green-600'}`}>
                            {loading ? 'Đang xác nhận...' : 'Xác nhận OTP'}
                        </button>
                    </form>
                )}

                {/* Bước 3: Đổi Mật Khẩu Mới */}
                {step === 3 && (
                    <form onSubmit={handleResetPassword}>
                        <div className="mb-4">
                            <label className="block text-gray-700 mb-2">Mật khẩu mới:</label>
                            <input type="password" required className="w-full px-3 py-2 border rounded-md"
                                   value={newPassword} onChange={(e) => setNewPassword(e.target.value)} minLength="6" />
                        </div>
                        <button type="submit" disabled={loading} className={`w-full text-white py-2 rounded-md transition ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-purple-500 hover:bg-purple-600'}`}>
                            {loading ? 'Đang đổi mật khẩu...' : 'Đổi mật khẩu'}
                        </button>
                    </form>
                )}

                <div className="mt-4 text-center">
                    <Link to="/login" className="text-blue-500 hover:underline">Quay lại đăng nhập</Link>
                </div>
            </div>
        </div>
    );
};

export default ForgotPassword;