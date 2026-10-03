/* eslint-disable @next/next/no-img-element */
"use client";
import Link from "next/link";
import React, { useEffect, ReactNode } from "react";
import { usePathname, useRouter } from "next/navigation";
import BubbleButton from "@components/ui/BubbleButton";
import { useAuth } from "@/hooks/useAuth";
import { useState } from "react";
import { FiUser } from "react-icons/fi";

interface AdminDashboardProps {
	children: ReactNode;
}

export function AdminDashboard({ children }: AdminDashboardProps) {
	const { getUsername } = useAuth();
	const [username, setUsername] = useState<string | null>(null);
	const pathname = usePathname();
	const router = useRouter();

	useEffect(() => {
		const name = getUsername();
		setUsername(name);
	}, [getUsername]);

	useEffect(() => {
		const navbarToggle = document.getElementById("navbar-toggle");
		const mobileNavbar = document.getElementById("mobile-navbar");

		if (navbarToggle && mobileNavbar) {
			const toggleMobileNavbar = () => {
				mobileNavbar.classList.toggle("hidden");
			};

			navbarToggle.addEventListener("click", toggleMobileNavbar);

			return () => {
				navbarToggle.removeEventListener("click", toggleMobileNavbar);
			};
		}
	}, []);

	const getNavItemClass = (path: string) => {
		return pathname === path ||
			(path === "/admin/influencers" &&
				pathname.startsWith("/admin/influencers")) ||
			(path === "/admin/clients" &&
				pathname.startsWith("/admin/clients")) ||
			(path === "/admin/offers" &&
				pathname.startsWith("/admin/offers")) ||
			(path === "/admin/invoices" &&
				pathname.startsWith("/admin/invoices")) ||
			(path === "/admin/admins" &&
				pathname.startsWith("/admin/admins")) ||
			(path === "/admin/settings" &&
				pathname.startsWith("/admin/settings"))
			? "py-2 px-4 bg-likeme-primary transition-all duration-500 ease-in-out text-base text-white font-semibold rounded-md"
			: "py-2 px-4 bg-transparent transition-all duration-500 ease-in-out text-sm hover:text-base text-gray-500 hover:bg-likeme-primary hover:text-white font-semibold rounded-md";
	};

	return (
		<div className="relative ">
			<nav className="bg-white py-4 px-6 w-full lg:shadow-none shadow-sm fixed ">
				<div className="flex items-center justify-between gap-1 sm:gap-6 lg:flex-row flex-col">
					<div className="flex justify-between items-center lg:w-auto w-full">
						<Link
							href="/admin"
							className="block hover:scale-105 transition-all duration-300 hover:bg-likeme-primary hover:p-1 hover:rounded-full"
						>
							<img
								src="/favicon.ico"
								alt="LikeMe logo"
								className="block object-cover w-10 h-10"
							/>
						</Link>
						<button
							id="navbar-toggle"
							type="button"
							className="inline-flex items-center p-2 ml-3 text-sm text-gray-500 rounded-lg lg:hidden hover:bg-gray-100 focus:outline-none "
							aria-controls="navbar-default"
							aria-expanded="false"
						>
							<span className="sr-only">Open main menu</span>
							<svg
								className="w-6 h-6"
								aria-hidden="true"
								fill="currentColor"
								viewBox="0 0 20 20"
								xmlns="http://www.w3.org/2000/svg"
							>
								<path
									fillRule="evenodd"
									d="M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM3 10a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zM3 15a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z"
									clipRule="evenodd"
								></path>
							</svg>
						</button>
					</div>
					<div
						id="mobile-navbar"
						className="hidden lg:flex flex-row w-full flex-1"
					>
						<ul className="text-center flex lg:flex-row flex-col lg:gap-2 xl:gap-4 gap-2 items-center lg:ml-auto">
							<li>
								<Link
									href="/admin"
									className={getNavItemClass("/admin")}
								>
									Dashboard
								</Link>
							</li>
							<li>
								<Link
									href="/admin/influencers"
									className={getNavItemClass(
										"/admin/influencers"
									)}
								>
									Influencers
								</Link>
							</li>
							<li>
								<Link
									href="/admin/clients"
									className={getNavItemClass(
										"/admin/clients"
									)}
								>
									Clients
								</Link>
							</li>
							<li>
								<Link
									href="/admin/admins"
									className={getNavItemClass("/admin/admins")}
								>
									Admins
								</Link>
							</li>
							<li>
								<Link
									href="/admin/offers"
									className={getNavItemClass("/admin/offers")}
								>
									Offers
								</Link>
							</li>
							<li>
								<Link
									href="/admin/orders"
									className={getNavItemClass("/admin/orders")}
								>
									Orders
								</Link>
							</li>
							<li>
								<Link
									href="/admin/invoices"
									className={getNavItemClass(
										"/admin/invoices"
									)}
								>
									Invoices
								</Link>
							</li>
							<li>
								<Link
									href="/admin/settings"
									className={getNavItemClass(
										"/admin/settings"
									)}
								>
									Settings
								</Link>
							</li>
						</ul>
						<div className="text-center mt-4 lg:mt-0 lg:flex items-center gap-1 sm:gap-4 lg:ml-auto">
							<div className="flex items-center lg:justify-start justify-center gap-1 sm:gap-2">
								<BubbleButton
									onClick={() => {
										router.push("/admin/settings");
									}}
								>
									<FiUser />
									My Account
								</BubbleButton>
							</div>
						</div>
					</div>
				</div>
			</nav>
			<div className="pt-[72px]">
				<div className="py-3.5 lg:px-8 px-3 bg-gray-50">
					<div className="block max-lg:pl-6">
						<h6 className="text-sm sm:text-lg font-semibold text-gray-900 whitespace-nowrap mb-1.5">
							Welcome back,
							<span className="text-likeme-primary text-base sm:text-lg font-semibold ml-1">
								{username || "User"}
							</span>
						</h6>
					</div>
				</div>
				<div className="w-full p-8 bg-gray-200 min-h-screen">
					<div className="border-gray-200 border rounded-lg p-4 bg-white">
						{children}
					</div>
				</div>
			</div>
		</div>
	);
}
