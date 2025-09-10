import { useForm } from "react-hook-form";
import { Link } from "react-router";

const LoginPage = () => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();

  const onSubmit = (data) => {
    console.log("Login submitted", data);
  };

  return (
    <div className="fixed inset-0 flex flex-col items-center justify-center overflow-hidden bg-gray-100 p-4 sm:p-6 lg:p-8">
      <div className="absolute -top-1/4 -right-1/6 h-1/2 w-1/2 rounded-full bg-blue-500/20 blur-3xl"></div>
      <div className="absolute -bottom-1/4 -left-1/4 h-1/2 w-1/2 rounded-full bg-blue-300/20 blur-3xl"></div>

      <div className="relative z-10 flex w-full max-w-md flex-col items-center">
        <div className="mb-4 text-center">
          <svg
            className="mx-auto h-16 w-16"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.5"
            viewBox="0 0 24 24"
          >
            <path
              d="M12 6.042A8.967 8.967 0 0 0 6 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 0 1 6 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 0 1 6-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0 0 18 18a8.967 8.967 0 0 0-6 2.292m0-14.25v14.25"
              strokeLinecap="round"
              strokeLinejoin="round"
            ></path>
          </svg>
          <h1 className="mt-2 text-3xl font-bold text-gray-900">
            Welcome Back
          </h1>
          <p className="mt-2 text-gray-600">
            Sign in to continue your learning journey.
          </p>
        </div>

        <div className="w-full rounded-2xl bg-white/70 p-8 backdrop-blur-lg border border-gray-200/80 shadow-lg">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label
                htmlFor="email"
                className="block text-sm font-medium text-gray-700"
              >
                Email
              </label>
              <div className="mt-1">
                <input
                  type="email"
                  placeholder="e.g., student@email.com"
                  {...register("email", { required: "Please enter an email" })}
                  className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300  outline-none focus:ring focus:ring-gray-900/80"
                />
                {errors.email && (
                  <p className="text-xs text-red-600 mt-1">
                    {errors.email?.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <label
                htmlFor="password"
                className="block text-sm font-medium text-gray-700"
              >
                Password
              </label>
              <div className="mt-1">
                <input
                  type="password"
                  placeholder="••••••••"
                  {...register("password", {
                    required: "Please enter a password",
                  })}
                  className="block w-full rounded-lg border-1 border-gray-300 bg-gray-100/50 p-3 text-gray-900 placeholder-gray-400 transition duration-300 outline-none focus:ring focus:ring-gray-900/80"
                />
                {errors.password && (
                  <p className="text-xs text-red-600 mt-1">
                    {errors.password?.message}
                  </p>
                )}
              </div>
            </div>

            <div className="flex items-center justify-end">
              <Link
                to="reset-password"
                href="#"
                className="font-medium text-sm text-blue-600 hover:text-blue-500"
              >
                Forgot your password?
              </Link>
            </div>

            <div>
              <button
                type="submit"
                className="flex w-full cursor-pointer justify-center rounded-lg bg-blue-600 px-3 py-3.5 text-sm font-semibold text-white shadow-lg transition duration-300 hover:bg-blue-700 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
              >
                Login
              </button>
            </div>
          </form>
        </div>

        <p className="mt-4 text-center text-sm text-gray-600">
          Don&apos;t have an account?{" "}
          <Link
            to="/signup"
            className="font-medium text-blue-600 hover:text-blue-500"
          >
            Sign up
          </Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
