"use client";
import React, { useState } from "react";
import { IconType } from "react-icons";
import {
	FiChevronDown,
	FiChevronsRight,
	FiHome,
	FiGift,
	FiUsers,
	FiShoppingBag,
	FiFileText,
	FiSettings,
} from "react-icons/fi";
import { motion } from "framer-motion";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import Image from "next/image";
import { useClientById } from "@/hooks/useClient";
import { useInfluencerById } from "@/hooks/useInfluencer";
import { getPhotoPath } from "@utils/photoPaths";

interface NavItem {
	icon: IconType;
	title: string;
	path: string;
}

const NAV_ITEMS: Record<string, NavItem[]> = {
	INFLUENCER: [
		{ icon: FiHome, title: "Dashboard", path: "/influencer" },
		{ icon: FiGift, title: "Offers", path: "/influencer/offers" },
		{ icon: FiUsers, title: "Clients", path: "/influencer/clients" },
		{ icon: FiShoppingBag, title: "Orders", path: "/influencer/orders" },
		{ icon: FiFileText, title: "Invoices", path: "/influencer/invoices" },
		{ icon: FiSettings, title: "Settings", path: "/influencer/settings" },
	],
	CLIENT: [
		{ icon: FiHome, title: "Dashboard", path: "/account" },
		{ icon: FiShoppingBag, title: "Orders", path: "/account/orders" },
		{ icon: FiFileText, title: "Invoices", path: "/account/invoices" },
		{ icon: FiSettings, title: "Settings", path: "/account/settings" },
	],
};

interface Props {
	children: React.ReactNode;
	role: "CLIENT" | "INFLUENCER";
}

export function ClientInfluencerDashboard({ children, role }: Props) {
	const navItems = NAV_ITEMS[role];

	return (
		<div className="flex bg-orange-50">
			<Sidebar navItems={navItems} />
			<main className="flex-1 p-8">{children}</main>
		</div>
	);
}

const Sidebar = ({ navItems }: { navItems: NavItem[] }) => {
	const [open, setOpen] = useState(true);
	const router = useRouter();
	const pathname = usePathname();

	return (
		<motion.nav
			layout
			className="sticky top-0 h-screen shrink-0 border-r border-slate-300 bg-white p-2"
			style={{
				width: open ? "225px" : "fit-content",
			}}
		>
			<TitleSection open={open} />

			<div className="space-y-1">
				{navItems.map((item) => (
					<Option
						key={item.path}
						Icon={item.icon}
						title={item.title}
						selected={pathname === item.path}
						onClick={() => router.push(item.path)}
						open={open}
					/>
				))}
			</div>

			<ToggleClose open={open} setOpen={setOpen} />
		</motion.nav>
	);
};

const Option = ({
	Icon,
	title,
	selected,
	onClick,
	open,
}: {
	Icon: IconType;
	title: string;
	selected: boolean;
	onClick: () => void;
	open: boolean;
}) => {
	return (
		<motion.button
			layout
			onClick={onClick}
			className={`relative flex h-10 w-full items-center rounded-md transition-colors ${
				selected
					? "bg-orange-100 text-orange-800"
					: "text-slate-500 hover:bg-slate-100"
			}`}
		>
			<motion.div
				layout
				className="grid h-full w-10 place-content-center text-lg"
			>
				<Icon />
			</motion.div>
			{open && (
				<motion.span
					layout
					initial={{ opacity: 0, y: 12 }}
					animate={{ opacity: 1, y: 0 }}
					transition={{ delay: 0.125 }}
					className="text-xs font-medium"
				>
					{title}
				</motion.span>
			)}
		</motion.button>
	);
};

const TitleSection = ({ open }: { open: boolean }) => {
	const { getUsername, getUserRole, getUserId } = useAuth();
	const username = getUsername();
	const role = getUserRole();
	const userId = getUserId();
	const router = useRouter();

	const { data: clientData } = useClientById(userId || 0);
	const { data: influencerData } = useInfluencerById(userId || 0);

	const profilePhoto =
		role === "CLIENT"
			? clientData?.profilePhotoPath
			: influencerData?.application?.profilePhotoPath;

	const handleTitleClick = () => {
		if (role === "CLIENT") {
			router.push("/account/settings");
		} else if (role === "INFLUENCER") {
			router.push("/influencer/settings");
		}
	};

	return (
		<div className="mb-3 border-b border-slate-300 pb-3">
			<div
				className="flex cursor-pointer items-center justify-between rounded-md transition-colors hover:bg-slate-100"
				onClick={handleTitleClick}
			>
				<div className="flex items-center gap-2">
					{profilePhoto ? (
						<Image
							src={getPhotoPath(profilePhoto)}
							alt="Profile"
							width={40}
							height={40}
							className="rounded-md object-cover"
						/>
					) : (
						<Image
							src="/logo.png"
							alt="Logo"
							width={40}
							height={40}
							className="rounded-md"
						/>
					)}
					{open && (
						<motion.div
							layout
							initial={{ opacity: 0, y: 12 }}
							animate={{ opacity: 1, y: 0 }}
							transition={{ delay: 0.125 }}
						>
							<span className="block text-xs font-semibold">
								{username ?? "Guest"}
							</span>
							<span className="block text-xs text-slate-500">
								{role?.toLowerCase() ?? "Not logged in"}
							</span>
						</motion.div>
					)}
				</div>
				{open && <FiChevronDown className="mr-2" />}
			</div>
		</div>
	);
};

const ToggleClose = ({
	open,
	setOpen,
}: {
	open: boolean;
	setOpen: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
	return (
		<motion.button
			layout
			onClick={() => setOpen((pv) => !pv)}
			className="absolute bottom-0 left-0 right-0 border-t border-slate-300 transition-colors hover:bg-slate-100"
		>
			<div className="flex items-center p-2">
				<motion.div
					layout
					className="grid size-10 place-content-center text-lg"
				>
					<FiChevronsRight
						className={`transition-transform ${
							open && "rotate-180"
						}`}
					/>
				</motion.div>
				{open && (
					<motion.span
						layout
						initial={{ opacity: 0, y: 12 }}
						animate={{ opacity: 1, y: 0 }}
						transition={{ delay: 0.125 }}
						className="text-xs font-medium"
					>
						Hide
					</motion.span>
				)}
			</div>
		</motion.button>
	);
};
